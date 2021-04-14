package org.dru.dusaf.store;

import org.dru.dusaf.cache.CacheFetcher;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class StoreFetcher<K, V> implements CacheFetcher<K, V> {
    private final Store<K, V> store;

    public StoreFetcher(final Store<K, V> store) {
        Objects.requireNonNull(store, "store");
        this.store = store;
    }

    @Override
    public V fetch(final K key) {
        return store.get(key);
    }

    @Override
    public Map<K, V> fetchAll(final Set<K> keys) {
        return store.get(keys);
    }
}
