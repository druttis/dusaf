package org.dru.dusaf.store;

import org.dru.dusaf.cache.Cache;
import org.dru.dusaf.cache.CacheFetcher;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class CachedStore<K, V> implements Store<K, V> {
    private final Store<K, V> store;
    private final Cache<K, V> cache;
    private final CacheFetcher<K, V> fetcher;

    public CachedStore(final Store<K, V> store, final Cache<K, V> cache) {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(cache, "cache");
        this.store = store;
        this.cache = cache;
        fetcher = new StoreCacheFetcher<>(store);
    }

    @Override
    public V get(final K key) {
        return cache.get(key, fetcher);
    }

    @Override
    public Map<K, V> get(final Set<K> keys) {
        return cache.getAll(keys, fetcher);
    }

    @Override
    public Map<K, V> get(final int offset, final int limit) {
        final Map<K, V> updated = store.get(offset, limit);
        cache.putAll(updated);
        return updated;
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operator) {
        final V updated = store.update(key, operator);
        cache.put(key, updated);
        return updated;
    }

    @Override
    public Map<K, V> update(final Set<K> keys, final UnaryOperator<V> operator) {
        final Map<K, V> updated = store.update(keys, operator);
        cache.putAll(updated);
        return updated;
    }

    @Override
    public Map<K, V> update(final Map<K, UnaryOperator<V>> operatorByKey) {
        final Map<K, V> updated = store.update(operatorByKey);
        cache.putAll(updated);
        return updated;
    }

    @Override
    public Map<K, V> update(final Set<K> keys, final BiFunction<K, V, V> operator) {
        final Map<K, V> updated = store.update(keys, operator);
        cache.putAll(updated);
        return updated;
    }

    @Override
    public int delete(final K key) {
        cache.remove(key);
        return store.delete(key);
    }

    @Override
    public int delete(final Set<K> keys) {
        cache.removeAll(keys);
        return store.delete(keys);
    }

    @Override
    public int delete() {
        cache.clear();
        return store.delete();
    }
}
