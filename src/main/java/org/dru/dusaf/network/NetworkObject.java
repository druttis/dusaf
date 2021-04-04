package org.dru.dusaf.network;

import java.net.SocketAddress;

/**
 * Base of all network objects, such as NetworkService, NetworkAcceptor, NetworkConnector, NetworkConnection.
 */
public interface NetworkObject {
    /**
     * Returns the address of this object.
     *
     * @return the address.
     */
    SocketAddress getAddress();

    /**
     * Returns an attachment object.
     *
     * @param type the attachment type.
     * @return the attachment object or null if no attachment is previously set via attach
     */
    <T> T attachment(Class<T> type);

    /**
     * Set an attachment to this object.
     *
     * @param type  the attachment type.
     * @param value the attachment.
     */
    <T> void attach(Class<T> type, T value);

    /**
     * Removes an attachment from this object.
     *
     * @param type the attachment type.
     */
    void detach(Class<?> type);

    /**
     * Closes the object.
     */
    void close();
}
