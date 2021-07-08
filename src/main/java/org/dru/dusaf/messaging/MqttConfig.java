package org.dru.dusaf.messaging;

public final class MqttConfig {
    private String broker;
    private boolean cleanSession;

    public MqttConfig(final String broker, final boolean cleanSession) {
        this.broker = broker;
        this.cleanSession = cleanSession;
    }

    public MqttConfig() {
    }

    public String getBroker() {
        return broker;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }
}
