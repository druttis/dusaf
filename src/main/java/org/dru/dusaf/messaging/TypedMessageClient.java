package org.dru.dusaf.messaging;

public interface TypedMessageClient {
    <T> void subscribe(Class<T> type, TypedMessageHandler<? super T> handler);

    <T> void unsubscribe(Class<T> type, TypedMessageHandler<? super T> handler);

    void publish(Object message);
}
