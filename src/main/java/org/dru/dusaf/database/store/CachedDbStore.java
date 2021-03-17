package org.dru.dusaf.database.store;

import org.dru.dusaf.cache.Cache;
import org.dru.dusaf.cache.CacheFetcher;

import java.util.function.UnaryOperator;

final class CachedDbStore<K, V> implements DbStore<K, V>, CacheFetcher<K, V> {
    private final DbStore<K, V> store;
    private Cache<K, V> cache;

    CachedDbStore(final DbStore<K, V> store) {
        this.store = store;
    }

    @Override
    public V get(final K key) {
        return cache.get(key);
    }

    @Override
    public V update(final K key, final UnaryOperator<V> updater) {
        final V result = store.update(key, updater);
        cache.put(key, result);
        return result;
    }

    @Override
    public void delete(final K key) {
        try {
            store.delete(key);
        } finally {
            cache.remove(key);
        }
    }

    @Override
    public V fetch(final K key) {
        return store.get(key);
    }

    void setCache(final Cache<K, V> cache) {
        this.cache = cache;
    }
}
