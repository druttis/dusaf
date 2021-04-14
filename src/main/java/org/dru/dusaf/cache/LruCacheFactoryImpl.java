package org.dru.dusaf.cache;

import org.dru.dusaf.time.TimeSupplier;

public final class LruCacheFactoryImpl implements LruCacheFactory {
    private final TimeSupplier timeSupplier;

    public LruCacheFactoryImpl(final TimeSupplier timeSupplier) {
        this.timeSupplier = timeSupplier;
    }

    @Override
    public <K, V> LruCache<K, V> newCache(final LruCacheConfig config) {
        return new LruCache<>(config, timeSupplier);
    }
}
