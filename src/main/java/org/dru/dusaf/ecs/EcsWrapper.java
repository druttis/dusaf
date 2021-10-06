package org.dru.dusaf.ecs;

final class EcsWrapper {
    private final EcsSystem system;
    private EcsFamily family;

    EcsWrapper(final EcsSystem system) {
        this.system = system;
    }

    void map(final EcsMappings mappings) {
        system.map(mappings);
    }

    void bind(final EcsEngine engine, final EcsMappings mappings) {
        final EcsBinder binder = new EcsBinder(mappings);
        system.bind(binder);
        final EcsAspect aspect = new EcsAspect(binder.anyOf, binder.allOf, binder.noneOf);
        family = engine.getFamily(aspect);
    }

    void initialize(final EcsEngine engine) {
        system.initialize(engine);
    }

    void update(final EcsEngine engine) {
        system.update(engine, family);
    }

    void destroy(final EcsEngine engine) {
        system.destroy(engine);
    }
}
