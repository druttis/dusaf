package org.dru.dusaf.network;

public final class NetworkException extends RuntimeException {
    public NetworkException(final String message) {
        super(message);
    }

    public NetworkException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
