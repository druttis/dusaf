package org.dru.dusaf.cache;

import java.time.Duration;

public interface LruCacheConfig {
    int capacity();

    Duration ttl();

    boolean storeNull();

    boolean accessOrder();
}
