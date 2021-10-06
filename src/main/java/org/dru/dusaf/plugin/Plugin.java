package org.dru.dusaf.plugin;

public interface Plugin {
    void initialize();

    void start();

    void stop();

    void destroy();
}
