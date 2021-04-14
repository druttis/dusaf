package org.dru.dusaf.cache;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ChainedCacheFetcher<K, V> implements CacheFetcher<K, V> {
    private final Cache<K, V> cache;
    private final CacheFetcher<K, V> fetcher;

    public ChainedCacheFetcher(final Cache<K, V> cache, final CacheFetcher<K, V> fetcher) {
        Objects.requireNonNull(cache, "secondary");
        this.cache = cache;
        this.fetcher = fetcher;
    }

    public ChainedCacheFetcher(final Cache<K, V> cache) {
        this(cache, null);
    }

    @Override
    public V fetch(final K key) {
        final V fetched = fetcher.fetch(key);
        cache.put(key, fetched);
        return fetched;
    }

    @Override
    public Map<K, V> fetchAll(final Set<K> keys) {
        final Map<K, V> fetched = fetcher.fetchAll(keys);
        cache.putAll(fetched);
        return fetched;
    }
}
