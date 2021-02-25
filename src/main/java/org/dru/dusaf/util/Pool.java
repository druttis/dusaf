package org.dru.dusaf.util;

import java.time.Duration;

public interface Pool<T> {
    T acquire(Duration duration) throws InterruptedException;

    T acquire() throws InterruptedException;

    void release(T item);

    int size();
}
