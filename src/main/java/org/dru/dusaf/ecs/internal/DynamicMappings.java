package org.dru.dusaf.ecs.internal;

import org.dru.dusaf.ecs.EcsMapping;
import org.dru.dusaf.ecs.EcsMappings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class DynamicMappings implements EcsMappings {
    private final AtomicInteger componentIdCounter;
    private final Map<Class<?>, EcsMapping<?>> map;

    public DynamicMappings() {
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
