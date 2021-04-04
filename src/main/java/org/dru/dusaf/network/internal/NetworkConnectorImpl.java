package org.dru.dusaf.network.internal;

import org.dru.dusaf.network.*;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

final class NetworkConnectorImpl extends AbstractNetworkObject<SocketChannel> implements NetworkConnector {
    private final NetworkConnectorHandler handler;

    NetworkConnectorImpl(final NetworkContext context, final SocketAddress address, final SocketChannel channel,
                         final NetworkProtocol protocol, final NetworkConnectorHandler handler) {
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

    NetworkConnectionHandler notifyOnConnect() {
        try {
            return handler.onConnect(this);
        } catch (final Exception exc) {
            logError("unhandled exception caught calling handler::onConnect, closing", exc);
            close();
            return null;
        }
    }
}
