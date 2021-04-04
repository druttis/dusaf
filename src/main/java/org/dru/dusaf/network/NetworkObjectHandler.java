package org.dru.dusaf.network;

public interface NetworkObjectHandler<T extends NetworkObject> {
    /**
     * Called when a network object is opened.
     *
     * @param object the opened network object.
     */
    void onOpen(T object);

    /**
     * Called when a network object is closed.
     *
     * @param object the closed network object.
     */
    void onClose(T object);
}
