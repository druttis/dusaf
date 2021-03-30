package org.dru.dusaf.database.store;

public interface DbStoreFactory {
    <K, V> DbStore<K, V> newStore(final String name, final Class<K> keyType, final Class<V> valueType);
}
