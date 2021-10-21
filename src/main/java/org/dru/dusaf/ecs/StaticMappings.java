package org.dru.dusaf.ecs;

import java.util.Map;

final class StaticMappings implements EcsMappings {
    private final Map<Class<?>, EcsMapping<?>> map;

    StaticMappings(final Map<Class<?>, EcsMapping<?>> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    public <T> EcsMapping<T> getMapping(final Class<T> componentType) {
        final EcsMapping<?> mapping = map.get(componentType);
        if (mapping == null) {
            throw new IllegalArgumentException("no such mapping: componentType=" + componentType);
        }
        return (EcsMapping<T>) mapping;
    }
}
