package org.dru.dusaf.network.internal;

public interface NetworkContext {
    void invokeLater(NetworkTask task);

    void logError(String message, Exception error);
}
