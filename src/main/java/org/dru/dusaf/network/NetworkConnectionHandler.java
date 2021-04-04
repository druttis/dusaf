package org.dru.dusaf.network;

public interface NetworkConnectionHandler extends NetworkObjectHandler<NetworkConnection> {
    void onRead(NetworkConnection connection);

    void onWrite(NetworkConnection connection);
}
