package org.dru.dusaf.messaging;

public interface TypedMessageHandler<T> {
    void handleMessage(T message);
}
