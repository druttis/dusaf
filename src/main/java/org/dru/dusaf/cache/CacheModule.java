package org.dru.dusaf.cache;

import org.dru.dusaf.inject.DependsOn;
import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.time.TimeModule;
import org.dru.dusaf.time.TimeSupplier;

import javax.inject.Singleton;

@DependsOn({TimeModule.class})
public final class CacheModule implements Module {
    @Provides
    @Singleton
    @Expose
    public LruCacheFactory getLruCacheFactory(final TimeSupplier timeSupplier) {
        return new LruCacheFactoryImpl(timeSupplier);
    }
}
