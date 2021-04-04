package org.dru.dusaf.network;

public interface NetworkConnectorHandler extends NetworkObjectHandler<NetworkConnector> {
    /**
     * Called when a connection has been established.
     *
     * @param connector the connector that established the connection.
     * @return a network connection handler to read/write data from, or null to close the connection.
     */
    NetworkConnectionHandler onConnect(NetworkConnector connector);
}
