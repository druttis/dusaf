package org.dru.dusaf.cache;

import org.dru.dusaf.time.TimeSupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

final class LruCache<K, V> implements Cache<K, V> {
    private final LruCacheConfig config;
    private final CacheFetcher<K, V> fetcher;
    private final TimeSupplier timeSupplier;
    private final Object monitor;
    private final Map<K, Item<V>> cache;

    LruCache(final LruCacheConfig config, final CacheFetcher<K, V> fetcher, final TimeSupplier timeSupplier) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(fetcher, "fetcher");
        Objects.requireNonNull(config.ttl(), "config.ttl()");
        if (config.capacity() < 1) {
            throw new IllegalArgumentException("config.capacity() has to be 1 or greater: " + config.capacity());
        }
        if (config.ttl().compareTo(Duration.ZERO) <= 0) {
            throw new IllegalArgumentException("config.ttl() has to be 1 or greater: " + config.ttl());
        }
        this.config = config;
        this.fetcher = fetcher;
        this.timeSupplier = timeSupplier;
        monitor = new Object();
        cache = new LinkedHashMap<K, Item<V>>(config.capacity(), 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<K, Item<V>> eldest) {
                return size() > config.capacity();
            }
        };
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public V get(final K key) {
        final V result;
        synchronized (monitor) {
            result = getInternal(key);
        }
        return result;
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        final Map<K, V> result = new HashMap<>();
        synchronized (monitor) {
            keys.forEach(key -> {
                final V value = getInternal(key);
                if (value != null) {
                    result.put(key, value);
                }
            });
        }
        return result;
    }

    @Override
    public void put(final K key, final V value) {
        synchronized (monitor) {
            putInternal(key, value);
        }
    }

    @Override
    public void putAll(final Map<K, V> valueByKey) {
        synchronized (monitor) {
            valueByKey.forEach(this::putInternal);
        }
    }

    @Override
    public void remove(final K key) {
        synchronized (monitor) {
            removeInternal(key);
        }
    }

    @Override
    public void removeAll(final Set<K> keys) {
        synchronized (monitor) {
            keys.forEach(this::removeInternal);
        }
    }

    @Override
    public void clear() {
        synchronized (monitor) {
            cache.clear();
        }
    }

    void clearExpired() {
        final Instant now = getNow();
        synchronized (monitor) {
            cache.values().removeIf(item -> {
                if (hasExpired(item, now)) {
                    return true;
                } else {
                    return false;
                }
            });
        }
    }

    private V getInternal(final K key) {
        final Instant now = timeSupplier.get();
        final Instant expires = getExpiry(now);
        final Item<V> result = cache.compute(key, ($, item) -> {
            if (item == null || hasExpired(item, now)) {
                final V value = fetcher.fetch(key);
                item = storeValue(expires, item, value);
            } else {
                if (config.accessOrder()) {
                    item.expires = expires;
                }
            }
            return item;
        });
        return (result != null ? result.value : null);
    }

    private void putInternal(final K key, final V value) {
        final Instant now = timeSupplier.get();
        final Instant expires = getExpiry(now);
        cache.compute(key, ($, item) -> storeValue(expires, item, value));
    }

    private void removeInternal(final K key) {
        cache.remove(key);
    }

    private Instant getNow() {
        return timeSupplier.get();
    }

    private Instant getExpiry(final Instant from) {
        return from.plus(config.ttl());
    }

    private boolean hasExpired(final Item<V> item, final Instant when) {
        return item.expires.isAfter(when);
    }

    private Duration getDuration(final Instant start) {
        return Duration.between(start, getNow());
    }

    private Item<V> storeValue(final Instant expires, Item<V> item, final V value) {
        if (value != null || config.storeNull()) {
            if (item == null) {
                item = new Item<>(value, expires);
            } else {
                item.value = value;
                item.expires = expires;
            }
        } else if (item != null) {
            item = null;
        }
        return item;
    }

    private static final class Item<V> {
        private V value;
        private Instant expires;

        private Item(final V value, final Instant expires) {
            this.value = value;
            this.expires = expires;
        }
    }
}
