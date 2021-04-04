package org.dru.dusaf.network;

import java.net.SocketAddress;

public interface NetworkAcceptorHandler extends NetworkObjectHandler<NetworkAcceptor> {
    /**
     * Called upon a connection request.
     *
     * @param address the socket address of the requesting connection.
     * @return A network connection handler or null, to cancel the connection request.
     */
    NetworkConnectionHandler onAccept(NetworkAcceptor acceptor, SocketAddress address);
}
