package org.dru.dusaf.cache;

import java.util.Map;
import java.util.Set;

public interface Cache<K, V> {
    V get(K key);

    Map<K, V> getAll(Set<K> keys);

    void put(K key, V value);

    void putAll(Map<K, V> valueByKey);

    void remove(K key);

    void removeAll(Set<K> keys);

    int size();

    void clear();
}
