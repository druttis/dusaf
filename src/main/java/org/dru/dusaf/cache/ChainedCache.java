package org.dru.dusaf.cache;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ChainedCache<K, V> implements Cache<K, V> {
    private final Cache<K, V> primary;
    private final Cache<K, V> secondary;

    public ChainedCache(final Cache<K, V> primary, final Cache<K, V> secondary) {
        Objects.requireNonNull(primary, "primary");
        Objects.requireNonNull(secondary, "secondary");
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public V get(final K key, final CacheFetcher<K, V> fetcher) {
        return primary.get(key, new ChainedCacheFetcher<>(secondary, fetcher));
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys, final CacheFetcher<K, V> fetcher) {
        return primary.getAll(keys, new ChainedCacheFetcher<>(secondary, fetcher));
    }

    @Override
    public void put(final K key, final V value) {
        primary.put(key, value);
        secondary.put(key, value);
    }

    @Override
    public void putAll(final Map<K, V> valueByKey) {
        primary.putAll(valueByKey);
        secondary.putAll(valueByKey);
    }

    @Override
    public void remove(final K key) {
        secondary.remove(key);
        primary.remove(key);
    }

    @Override
    public void removeAll(final Set<K> keys) {
        secondary.removeAll(keys);
        primary.removeAll(keys);
    }

    @Override
    public void clear() {
        secondary.clear();
        primary.clear();
    }
}
