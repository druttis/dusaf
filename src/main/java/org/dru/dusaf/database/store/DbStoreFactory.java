package org.dru.dusaf.database.store;

import org.dru.dusaf.cache.LruCacheConfig;

public interface DbStoreFactory {
    <K, V> DbStore<K, V> newStore(String name, Class<K> keyType, Class<V> valueType, boolean exploded);

    <K, V> DbStore<K, V> newCachedStore(String name, Class<K> keyType, Class<V> valueType, boolean exploded,
                                        LruCacheConfig config);
}
