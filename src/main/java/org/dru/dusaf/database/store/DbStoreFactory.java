package org.dru.dusaf.database.store;

public interface DbStoreFactory {
    <K, V> DbStore<K, V> newStore(String name, Class<K> keyType, Class<V> valueType, boolean exploded);
}
