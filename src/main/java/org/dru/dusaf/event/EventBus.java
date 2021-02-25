package org.dru.dusaf.event;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface EventBus {
    void addListener(Consumer<Object> listener, EventDeliverer deliverer);

    void addListener(Consumer<Object> listener);

    void removeListener(Consumer<Object> listener);

    <E> void addListener(Class<E> type, Consumer<? super E> listener, EventDeliverer deliverer);

    <E> void addListener(Class<E> type, Consumer<? super E> listener);

    <E> void removeListener(Class<E> type, Consumer<? super E> listener);

    void appendIntoListenersByDeliverer(Class<?> type, Map<EventDeliverer,
            List<Consumer<Object>>> listenersByDeliverer);

    void fireEvent(Object event);
}
