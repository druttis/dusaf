package org.dru.dusaf.messaging;

public final class JsonMessage<T> {
    private String sender;
    private T message;

    public JsonMessage(final String sender, final T message) {
        this.sender = sender;
        this.message = message;
    }

    public JsonMessage() {
    }

    public String getSender() {
        return sender;
    }

    public T getMessage() {
        return message;
    }
}
