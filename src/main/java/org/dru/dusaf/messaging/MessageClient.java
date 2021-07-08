package org.dru.dusaf.messaging;

public interface MessageClient {
    void subscribe(String topic, MessageHandler handler);

    void unsubscribe(String topic, MessageHandler handler);

    void publish(String topic, byte[] content);
}
