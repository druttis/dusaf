package org.dru.dusaf.event;

import java.util.Collection;
import java.util.function.Consumer;

public interface EventDeliverer {
    void deliverEvent(Object event, Collection<Consumer<Object>> listeners);
}
