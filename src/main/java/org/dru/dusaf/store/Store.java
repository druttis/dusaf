package org.dru.dusaf.store;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface Store<K, V> {
    V get(K key);

    Map<K, V> get(Set<K> keys);

    Map<K, V> get(int offset, int limit);

    V update(K key, UnaryOperator<V> operator);

    Map<K, V> update(Set<K> keys, UnaryOperator<V> operator);

    Map<K, V> update(Map<K, UnaryOperator<V>> operatorByKey);

    Map<K, V> update(Set<K> keys, BiFunction<K, V, V> operator);

    int delete(K key);

    int delete(Set<K> keys);

    int delete();
}
