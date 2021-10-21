package org.dru.dusaf.ecs;

import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;

import javax.inject.Singleton;

public final class EcsModule implements Module {
    public EcsModule() {
    }

    @Provides
    @Singleton
    @Expose
    public EcsEngineBuilderSupplier getEcsEngineBuilderSupplier() {
        return new DefaultEngineBuilderSupplier();
    }
}
