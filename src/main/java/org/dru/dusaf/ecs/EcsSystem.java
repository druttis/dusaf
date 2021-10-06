package org.dru.dusaf.ecs;

public interface EcsSystem {
    void map(EcsMappings mappings);

    void bind(EcsBinder binder);

    void initialize(EcsEngine engine);

    void update(EcsEngine engine, Iterable<EcsEntity> entities);

    void destroy(EcsEngine engine);
}
