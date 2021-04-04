package org.dru.dusaf.network.internal;

import org.dru.dusaf.network.NetworkAcceptor;
import org.dru.dusaf.network.NetworkAcceptorHandler;
import org.dru.dusaf.network.NetworkConnectionHandler;
import org.dru.dusaf.network.NetworkProtocol;

import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

final class NetworkAcceptorImpl extends AbstractNetworkObject<ServerSocketChannel> implements NetworkAcceptor {
    private final NetworkAcceptorHandler handler;

    NetworkAcceptorImpl(final NetworkContext context, final SocketAddress address, final ServerSocketChannel channel,
                        final NetworkProtocol protocol, final NetworkAcceptorHandler handler) {
        super(context, address, channel, protocol);
        this.handler = handler;
    }

    @Override
    void notifyOnOpen() {
        handler.onOpen(this);
    }

    @Override
    void notifyOnClose() {
        handler.onClose(this);
    }

    public NetworkConnectionHandler notifyOnAccept(final SocketAddress address) {
        try {
            return handler.onAccept(this, address);
        } catch (final Exception exc) {
            logError("unhandled exception caught calling handler::onAccept", exc);
            return null;
        }
    }
}
