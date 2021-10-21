package org.dru.dusaf.ecs;

final class DefaultEngineBuilderSupplier implements EcsEngineBuilderSupplier {
    public DefaultEngineBuilderSupplier() {
    }

    @Override
    public EcsEngineBuilder get() {
        return new DefaultEngineBuilder();
    }
}
