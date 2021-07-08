package org.dru.dusaf.messaging;

public final class MqttConfig {
    public String[] serverURIs = {"tcp://127.0.0.1:1883"};
    public boolean cleanSession = true;
    public boolean automaticReconnect = true;
    public int connectionTimeout = 10;
    public int keepAliveInterval = 10;
    public String password = "";

    public MqttConfig() {
    }
}
