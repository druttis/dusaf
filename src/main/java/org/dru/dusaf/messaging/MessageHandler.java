package org.dru.dusaf.messaging;

public interface MessageHandler {
    void handleMessage(String topic, byte[] content);
}
