package org.dru.dusaf.network.internal;

import org.dru.dusaf.concurrent.task.TaskExecutor;
import org.dru.dusaf.concurrent.task.TaskManager;
import org.dru.dusaf.network.*;
import org.dru.dusaf.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.channels.SelectionKey.*;

public final class NetworkServiceImpl implements NetworkService, NetworkContext {
    private static final Logger logger = LoggerFactory.getLogger(NetworkServiceImpl.class);

    private final Selector selector;
    private final List<NetworkTask> pendingTasks;

    public NetworkServiceImpl(final TaskManager taskManager) {
        try {
            selector = Selector.open();
            final TaskExecutor executor = taskManager.getExecutor("network-service");
            executor.submit(String.format("%s::run", getClass().getName()), this::run);
        } catch (final Exception exc) {
            logError("failed to create network-service", exc);
            throw new NetworkException("failed to create service", exc);
        }
        pendingTasks = new ArrayList<>();
    }

    @Override
    public NetworkAcceptor createAcceptor(final String address, final int port, final NetworkProtocol protocol,
                                          final NetworkAcceptorHandler handler) {
        checkSelector();
        Objects.requireNonNull(address, "address");
        Objects.requireNonNull(protocol, "protocol");
        Objects.requireNonNull(handler, "handler");
        final SocketAddress local = new InetSocketAddress(address, port);
        try {
            final ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(local);
            final NetworkAcceptorImpl acceptor = new NetworkAcceptorImpl(this, local, channel, protocol, handler);
            invokeLater(() -> {
                channel.register(selector, OP_ACCEPT, acceptor);
                acceptor.notifyOnOpen();
            });
            return acceptor;
        } catch (final IOException exc) {
            logError("failed to create network-acceptor", exc);
            throw new NetworkException("failed to create acceptor", exc);
        }
    }

    @Override
    public NetworkConnector createConnector(final String address, final int port, final NetworkProtocol protocol,
                                            final NetworkConnectorHandler handler) {
        checkSelector();
        Objects.requireNonNull(address, "address");
        Objects.requireNonNull(protocol, "protocol");
        Objects.requireNonNull(handler, "handler");
        final SocketAddress endpoint = new InetSocketAddress(address, port);
        try {
            final SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            final NetworkConnectorImpl connector = new NetworkConnectorImpl(this, endpoint, channel, protocol, handler);
            invokeLater(() -> {
                channel.register(selector, OP_CONNECT, connector);
                channel.connect(endpoint);
                connector.notifyOnOpen();
            });
            return connector;
        } catch (final IOException exc) {
            logError("failed to create network-connector", exc);
            throw new NetworkException("failed to create connector", exc);
        }
    }

    @Override
    public void invokeLater(final NetworkTask task) {
        Objects.requireNonNull(task, "task");
        synchronized (pendingTasks) {
            pendingTasks.add(task);
            selector.wakeup();
        }
    }

    @Override
    public void logError(final String message, final Exception error) {
        logger.error(message, error);
    }

    private void checkSelector() {
        if (selector == null) {
            throw new NetworkException("selector not created");
        } else if (!selector.isOpen()) {
            throw new NetworkException("selector is closed");
        }
    }

    private void invokePendingTasks() {
        NetworkTask[] tasks;
        synchronized (pendingTasks) {
            tasks = pendingTasks.toArray(new NetworkTask[0]);
            pendingTasks.clear();
        }
        Stream.of(tasks).forEach(task -> {
            try {
                task.run();
            } catch (final Exception exc) {
                logError("unhandled exception caught caused by invoked task", exc);
            }
        });
    }

    private void handleInvalidSelectionKey(final SelectionKey selectionKey) {
        final NetworkObject object = (NetworkObject) selectionKey.attachment();
        object.close();
        selectionKey.cancel();
    }

    private void handleAcceptableSelectionKey(final SelectionKey selectionKey) throws IOException {
        final ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
        final SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        final Socket socket = channel.socket();
        final SocketAddress address = socket.getRemoteSocketAddress();
        final NetworkAcceptorImpl acceptor = (NetworkAcceptorImpl) selectionKey.attachment();
        final NetworkConnectionHandler handler = acceptor.notifyOnAccept(address);
        if (handler != null) {
            final NetworkConnectionImpl connection = new NetworkConnectionImpl(this, address, channel,
                    acceptor.getProtocol(), handler);
            channel.register(selector, OP_READ | OP_WRITE, connection);
            connection.notifyOnOpen();
        } else {
            socket.close();
        }
    }

    private void handleConnectableSelectionKey(final SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        final Socket socket = channel.socket();
        final SocketAddress address = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
        final NetworkConnectorImpl connector = (NetworkConnectorImpl) selectionKey.attachment();
        final NetworkConnectionHandler handler = connector.notifyOnConnect();
        if (handler != null) {
            while (channel.isConnectionPending()) {
                channel.finishConnect();
            }
            final NetworkConnectionImpl connection = new NetworkConnectionImpl(this, address, channel,
                    connector.getProtocol(), handler);
            selectionKey.interestOps(OP_READ | OP_WRITE);
            selectionKey.attach(connection);
            connection.notifyOnOpen();
        } else {
            socket.close();
        }
    }

    private void handleReadableSelectionKey(final SelectionKey selectionKey) {
        final NetworkConnectionImpl connection = (NetworkConnectionImpl) selectionKey.attachment();
        connection.notifyOnRead();
    }

    private void handleWritableSelectionKey(final SelectionKey selectionKey) {
        final NetworkConnectionImpl connection = (NetworkConnectionImpl) selectionKey.attachment();
        connection.notifyOnWrite();
    }

    private void handleSelectedKeys(final Set<SelectionKey> selectedKeys) {
        final Iterator<SelectionKey> it = selectedKeys.iterator();
        while (it.hasNext()) {
            final SelectionKey selectionKey = it.next();
            it.remove();
            final NetworkObject object = (NetworkObject) selectionKey.attachment();
            try {
                if (!selectionKey.isValid()) {
                    handleInvalidSelectionKey(selectionKey);
                } else if (selectionKey.isAcceptable()) {
                    handleAcceptableSelectionKey(selectionKey);
                } else if (selectionKey.isConnectable()) {
                    handleConnectableSelectionKey(selectionKey);
                } else if (selectionKey.isReadable()) {
                    handleReadableSelectionKey(selectionKey);
                } else if (selectionKey.isWritable()) {
                    handleWritableSelectionKey(selectionKey);
                } else {
                    throw new NetworkException("unhandled selectionKey, closing it: " + selectionKey.readyOps());
                }
            } catch (final Exception exc) {
                object.close();
            }
        }
    }

    private void run() {
        while (selector.isOpen()) {
            invokePendingTasks();
            try {
                final int numSelectionKeys = selector.select();
                if (numSelectionKeys <= 0) {
                    continue;
                }
            } catch (final Exception exc) {
                IOUtils.close(selector);
                logger.error("unhandled exception caught, network-service is shutting down.", exc);
                continue;
            }
            handleSelectedKeys(selector.selectedKeys());
        }
    }
}
