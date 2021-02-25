package org.dru.dusaf.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class DefaultEventBus implements EventBus {
    private final EventBus parent;
    private final Map<EventDeliverer, List<Consumer<Object>>> listenersByDeliverer;
    private final Map<Class<?>, Map<EventDeliverer, List<Consumer<Object>>>> listenersByDelivererByType;

    public DefaultEventBus(final EventBus parent) {
        this.parent = parent;
        listenersByDeliverer = new ConcurrentHashMap<>();
        listenersByDelivererByType = new ConcurrentHashMap<>();
    }

    DefaultEventBus() {
        this(null);
    }

    @Override
    public void addListener(final Consumer<Object> listener, final EventDeliverer deliverer) {
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(deliverer, "deliverer");
        addListenerToDeliverer(listener, deliverer, listenersByDeliverer);
    }

    @Override
    public void addListener(final Consumer<Object> listener) {
        addListener(listener, ImmediateEventDeliverer.INSTANCE);
    }

    @Override
    public void removeListener(final Consumer<? super Object> listener) {
        Objects.requireNonNull(listener, "listener");
        removeListenerFromDeliverer(listener, listenersByDeliverer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> void addListener(final Class<E> type, final Consumer<? super E> listener,
                                final EventDeliverer deliverer) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(listener, "listener");
        Objects.requireNonNull(deliverer, "deliverer");
        addListenerToDeliverer((Consumer<Object>) listener, deliverer,
                listenersByDelivererByType.computeIfAbsent(type, $ -> new ConcurrentHashMap<>()));
    }

    @Override
    public <E> void addListener(final Class<E> type, final Consumer<? super E> listener) {
        addListener(type, listener, ImmediateEventDeliverer.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> void removeListener(final Class<E> type, final Consumer<? super E> listener) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(listener, "listener");
        listenersByDelivererByType.computeIfPresent(type, ($, listenersByDeliverer) -> {
            removeListenerFromDeliverer((Consumer<Object>) listener, listenersByDeliverer);
            return (listenersByDeliverer.isEmpty() ? null : listenersByDeliverer);
        });
    }

    @Override
    public void appendIntoListenersByDeliverer(final Class<?> type, final Map<EventDeliverer,
            List<Consumer<Object>>> target) {
        appendIntoListenersByDeliverer(listenersByDeliverer, target);
        final Map<EventDeliverer, List<Consumer<Object>>> listenersByDeliverer =
                listenersByDelivererByType.get(type);
        if (listenersByDeliverer != null) {
            appendIntoListenersByDeliverer(listenersByDeliverer, target);
        }
    }

    @Override
    public void fireEvent(final Object event) {
        Objects.requireNonNull(event, "event");
        final Class<?> type = event.getClass();
        final Map<EventDeliverer, List<Consumer<Object>>> listenersByDeliverer = new HashMap<>();
        appendIntoListenersByDeliverer(type, listenersByDeliverer);
        if (parent != null) {
            parent.appendIntoListenersByDeliverer(type, listenersByDeliverer);
        }
        listenersByDeliverer.forEach((deliverer, listeners) -> deliverer.deliverEvent(event, listeners));
    }

    private void addListenerToDeliverer(final Consumer<Object> listener, final EventDeliverer deliverer,
                                        final Map<EventDeliverer, List<Consumer<Object>>> listenersByDeliverer) {
        listenersByDeliverer.computeIfAbsent(deliverer, $ -> new CopyOnWriteArrayList<>()).add(listener);
    }

    private void removeListenerFromDeliverer(final Consumer<Object> listener,
                                             final Map<EventDeliverer, List<Consumer<Object>>> listenersByDeliverer) {
        listenersByDeliverer.values().removeIf(list -> list.remove(listener) && list.isEmpty());
    }

    public void appendIntoListenersByDeliverer(final Map<EventDeliverer, List<Consumer<Object>>> source,
                                               final Map<EventDeliverer, List<Consumer<Object>>> target) {
        source.forEach((deliverer, listeners) -> target.computeIfAbsent(deliverer, $ -> new CopyOnWriteArrayList<>())
                .addAll(listeners));
    }
}
