package org.dru.dusaf.messaging;

import org.apache.log4j.Logger;
import org.dru.dusaf.host.HostNameProvider;
import org.dru.dusaf.json.conf.JsonConf;
import org.eclipse.paho.client.mqttv3.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MqttMessageClient implements MessageClient {
    private static final Logger logger = Logger.getLogger(MqttMessageClient.class);

    private final MqttClient mqttClient;
    private final Map<String, List<MessageHandler>> handlersByTopic;
    private final Receiver receiver;

    public MqttMessageClient(final JsonConf jsonConfig, final HostNameProvider hostNameProvider) {
        final MqttConfig conf = jsonConfig.get(MqttConfig.class, "dusaf-mqtt", MqttConfig::new);
        final MqttConnectOptions ops = new MqttConnectOptions();
        ops.setCleanSession(conf.cleanSession);
        ops.setAutomaticReconnect(conf.automaticReconnect);
        ops.setConnectionTimeout(conf.connectionTimeout);
        ops.setKeepAliveInterval(conf.keepAliveInterval);
        ops.setPassword(conf.password.toCharArray());
        ops.setServerURIs(conf.serverURIs);
        try {
            mqttClient = new MqttClient(conf.serverURIs[0], hostNameProvider.getCanonicalName());
            logger.info("connecting to mqtt broker...");
            mqttClient.connect(ops);
            logger.info("connected to mqtt broker.");
        } catch (final MqttException exc) {
            throw new RuntimeException("failed to start mqtt client", exc);
        }
        handlersByTopic = new ConcurrentHashMap<>();
        receiver = new Receiver();
    }

    @Override
    public void subscribe(final String topic, final MessageHandler handler) {
        Objects.requireNonNull(topic, "topic");
        Objects.requireNonNull(handler, "handler");
        handlersByTopic.computeIfAbsent(topic, $ -> {
            try {
                mqttClient.subscribe(topic, receiver);
                logger.info("subscribed to topic: " + topic);
            } catch (final MqttException exc) {
                logger.error("failed to subscribe to topic: " + topic, exc);
                throw new RuntimeException("failed to subscribe to topic: " + topic, exc);
            }
            return new CopyOnWriteArrayList<>();
        }).add(handler);
    }

    @Override
    public void unsubscribe(final String topic, final MessageHandler handler) {
        Objects.requireNonNull(topic, "topic");
        Objects.requireNonNull(handler, "handler");
        handlersByTopic.computeIfPresent(topic, ($, handlers) -> {
            handlers.remove(handler);
            if (handlers.isEmpty()) {
                try {
                    mqttClient.unsubscribe(topic);
                    logger.info("unsubscribed from topic: " + topic);
                } catch (final MqttException exc) {
                    logger.error("failed to unsubscribe from topic: " + topic, exc);
                    throw new RuntimeException("failed to unsubscribe from topic: " + topic, exc);
                }
                return null;
            }
            return handlers;
        });
    }

    @Override
    public void publish(final String topic, final byte[] content) {
        try {
            mqttClient.publish(topic, content, 2, false);
            logger.debug("published to topic: " + topic);
        } catch (final MqttException exc) {
            logger.error("failed to publish to topic: " + topic, exc);
        }
    }

    private final class Receiver implements IMqttMessageListener {
        @Override
        public void messageArrived(final String topic, final MqttMessage message) throws Exception {
            final List<MessageHandler> handlers = handlersByTopic.get(topic);
            if (handlers != null) {
                logger.debug("message received on topic: " + topic);
                final byte[] content = message.getPayload();
                handlers.forEach(handler -> {
                    try {
                        handler.handleMessage(topic, content);
                    } catch (final RuntimeException exc) {
                        logger.error("unhandled runtime-exception caught:", exc);
                    }
                });
            } else {
                logger.warn("message received on unhandled topic: " + topic);
            }
        }
    }
}
