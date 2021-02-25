package org.dru.dusaf.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Consumer;

public enum ImmediateEventDeliverer implements EventDeliverer {
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void deliverEvent(final Object event, final Collection<Consumer<Object>> listeners) {
        listeners.forEach(listener -> {
            try {
                listener.accept(event);
            } catch (final RuntimeException exc) {
                logger.error("unhandled runtime-exception caught", exc);
            }
        });
    }
}
