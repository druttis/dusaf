package org.dru.dusaf.network.internal;

import org.dru.dusaf.network.NetworkObject;
import org.dru.dusaf.network.NetworkProtocol;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.NetworkChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractNetworkObject<C extends NetworkChannel> implements NetworkObject {
    private final NetworkContext context;
    private final SocketAddress address;
    private final C channel;
    private final NetworkProtocol protocol;
    private final Map<Class<?>, Object> attachmentByType;

    AbstractNetworkObject(final NetworkContext context, final SocketAddress address, final C channel,
                          final NetworkProtocol protocol) {
        this.context = context;
        this.address = address;
        this.channel = channel;
        this.protocol = protocol;
        attachmentByType = new ConcurrentHashMap<>();
    }

    @Override
    public final SocketAddress getAddress() {
        return address;
    }

    @Override
    public final <T> T attachment(final Class<T> type) {
        return type.cast(attachmentByType.get(type));
    }

    @Override
    public final <T> void attach(final Class<T> type, final T value) {
        attachmentByType.put(type, value);
    }

    @Override
    public final void detach(final Class<?> type) {
        attachmentByType.remove(type);
    }

    @Override
    public void close() {
        context.invokeLater(() -> {
            channel.close();
        });
        context.invokeLater(this::notifyOnClose);
    }

    final NetworkContext getContext() {
        return context;
    }

    final NetworkProtocol getProtocol() {
        return protocol;
    }

    final C getChannel() {
        return channel;
    }

    final void invoke(final NetworkTask task) {
        context.invokeLater(task);
    }

    final void logError(final String message, final Exception error) {
        context.logError(message, error);
    }

    abstract void notifyOnOpen();

    abstract void notifyOnClose();
}
