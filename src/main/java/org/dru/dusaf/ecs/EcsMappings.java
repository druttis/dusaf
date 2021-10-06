package org.dru.dusaf.ecs;

public interface EcsMappings {
    <T> EcsMapping<T> getMapping(final Class<T> componentType);
}
