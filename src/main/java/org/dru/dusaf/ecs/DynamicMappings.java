package org.dru.dusaf.ecs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

final class DynamicMappings implements EcsMappings {
    private final AtomicInteger componentIdCounter;
    private final Map<Class<?>, EcsMapping<?>> map;

    DynamicMappings() {
        componentIdCounter = new AtomicInteger();
        map = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> EcsMapping<T> getMapping(final Class<T> componentType) {
        return (EcsMapping<T>) map.computeIfAbsent(componentType, $
                -> new EcsMapping<>(componentIdCounter.getAndIncrement(), componentType));
    }
}
