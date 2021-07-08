package org.dru.dusaf.messaging;

import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.json.JsonSerializerSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class JsonMessageClient implements TypedMessageClient {
    private static final Logger logger = LoggerFactory.getLogger(JsonMessageClient.class);

    private final MessageClient messageClient;
    private final JsonSerializer jsonSerializer;
    private final Map<Class<?>, Subscriptions<?>> subscriptionsByType;

    public JsonMessageClient(final MessageClient messageClient, final JsonSerializerSupplier jsonSerializerSupplier) {
        this.messageClient = messageClient;
        jsonSerializer = jsonSerializerSupplier.get();
        subscriptionsByType = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void subscribe(final Class<T> type, final TypedMessageHandler<? super T> handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        ((Subscriptions<T>) subscriptionsByType.computeIfAbsent(type, $ -> {
            final Subscriptions<T> subscriptions = new Subscriptions<>(type);
            messageClient.subscribe(type.getName(), subscriptions);
            return subscriptions;
        })).subscribe(handler);
    }

    @Override
    public <T> void unsubscribe(final Class<T> type, final TypedMessageHandler<? super T> handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        subscriptionsByType.computeIfPresent(type, ($, subscriptions) -> {
            if (subscriptions.unsubscribe(handler)) {
                messageClient.unsubscribe(type.getName(), subscriptions);
                return null;
            }
            return subscriptions;
        });
    }

    @Override
    public void publish(final Object message) {
        Objects.requireNonNull(message, "message");
        final byte[] payload;
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            jsonSerializer.writeObject(out, message);
            payload = out.toByteArray();
        } catch (final IOException exc) {
            logger.error("failed to serialize message:", exc);
            return;
        }
        messageClient.publish(message.getClass().getName(), payload);
    }

    private final class Subscriptions<T> implements MessageHandler {
        private final Class<T> type;
        private final List<TypedMessageHandler<? super T>> handlers;

        private Subscriptions(final Class<T> type) {
            this.type = type;
            handlers = new CopyOnWriteArrayList<>();
        }

        private void subscribe(final TypedMessageHandler<? super T> handler) {
            handlers.add(handler);
        }

        private boolean unsubscribe(final TypedMessageHandler<?> handler) {
            return handlers.remove(handler) && handlers.isEmpty();
        }

        @Override
        public void handleMessage(final String topic, final byte[] content) {
            final T message;
            try (final InputStream in = new ByteArrayInputStream(content)) {
                message = jsonSerializer.readObject(in, type);
            } catch (final IOException exc) {
                logger.error("failed to de-serialize error:", exc);
                return;
            }
            handlers.forEach(handler -> {
                try {
                    handler.handleMessage(message);
                } catch (final RuntimeException exc) {
                    logger.error("unhandled runtime-exception caught:", exc);
                }
            });
        }
    }
}
