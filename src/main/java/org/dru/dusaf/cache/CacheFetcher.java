package org.dru.dusaf.cache;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface CacheFetcher<K, V> {
    V fetch(K key);

    default Map<K, V> fetchAll(Set<K> keys) {
        return keys.stream().collect(Collectors.toMap(k -> k, this::fetch));
    }
}
