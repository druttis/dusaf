package org.dru.dusaf.cache;

public interface LruCacheFactory {
    <K, V> LruCache<K, V> newCache(LruCacheConfig config, CacheFetcher<K, V> fetcher);
}
