package org.dru.dusaf.network.internal;

import org.dru.dusaf.network.NetworkConnection;
import org.dru.dusaf.network.NetworkConnectionHandler;
import org.dru.dusaf.network.NetworkException;
import org.dru.dusaf.network.NetworkProtocol;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

final class NetworkConnectionImpl extends AbstractNetworkObject<SocketChannel> implements NetworkConnection {
    private final NetworkConnectionHandler handler;
    private final Queue<ByteBuffer> incoming;
    private final Queue<ByteBuffer> outgoing;
    private volatile boolean closing;

    NetworkConnectionImpl(final NetworkContext context, final SocketAddress address, final SocketChannel channel,
                          final NetworkProtocol protocol, final NetworkConnectionHandler handler) {
        super(context, address, channel, protocol);
        this.handler = handler;
        incoming = new ConcurrentLinkedQueue<>();
        outgoing = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void close() {
        closing = true;
    }

    @Override
    void notifyOnOpen() {
        handler.onOpen(this);
    }

    @Override
    void notifyOnClose() {
        handler.onClose(this);
    }

    @Override
    public void read(final ByteBuffer target) {
        Objects.requireNonNull(target, "target");
        checkClosing();
        final ByteBuffer source = incoming.peek();
        if (source != null) {
            target.put(source);
            if (!source.hasRemaining()) {
                incoming.remove();
            }
        }
        target.flip();
    }

    @Override
    public void write(final ByteBuffer source) {
        Objects.requireNonNull(source, "source");
        checkClosing();
        final ByteBuffer target = ByteBuffer.allocate(source.limit());
        source.mark();
        target.put(source);
        source.reset();
        target.flip();
        outgoing.offer(target);
    }

    void notifyOnRead() {
        if (readFromChannel()) {
            try {
                handler.onRead(this);
            } catch (final Exception exc) {
                logError("unhandled exception caught calling handler::onRead", exc);
            }
        }
    }

    void notifyOnWrite() {
        writeToChannel();
        try {
            handler.onWrite(this);
        } catch (final RuntimeException exc) {
            logError("unhandled exception caught calling handler::onWrite", exc);
            throw exc;
        }
    }

    private void checkClosing() {
        if (closing) {
            throw new NetworkException("closing");
        }
    }

    private boolean readFromChannel() {
        final ByteBuffer target = ByteBuffer.allocate(1024);
        final int bytesRead;
        try {
            bytesRead = getChannel().read(target);
        } catch (final IOException exc) {
            throw new NetworkException("failed to read", exc);
        }
        if (bytesRead == -1) {
            super.close();
        } else if (bytesRead > 0) {
            target.flip();
            incoming.offer(target);
        }
        return (bytesRead > 0);
    }

    private void writeToChannel() {
        final ByteBuffer source = outgoing.peek();
        if (source != null) {
            try {
                getChannel().write(source);
                if (!source.hasRemaining()) {
                    outgoing.remove();
                }
            } catch (final IOException exc) {
                throw new NetworkException("failed to write", exc);
            }
        }
        if (closing && outgoing.isEmpty()) {
            super.close();
        }
    }
}
