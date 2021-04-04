package org.dru.dusaf.network;

public interface NetworkService {
    /**
     * Creates a new acceptor
     *
     * @param address  the address to accept connections to.
     * @param port     the port to accept connections on.
     * @param protocol a protocol on which connections accepted by this acceptor will be using.
     * @param handler  a handler to receive acceptor related notifications.
     * @return the new acceptor.
     * @throws NetworkException if anything goes bananas.
     */
    NetworkAcceptor createAcceptor(String address, int port, NetworkProtocol protocol,
                                   NetworkAcceptorHandler handler);

    /**
     * Creates a new connector.
     *
     * @param address  the address to connect to.
     * @param port     the port to connect to.
     * @param protocol a protocol on which connections accepted by this connector will be using.
     * @param handler  a handler to receive connector related notifications.
     * @return the new connector.
     * @throws NetworkException if anything goes bananas.
     */
    NetworkConnector createConnector(String address, int port, NetworkProtocol protocol,
                                     final NetworkConnectorHandler handler);
}
