package org.dru.dusaf.concurrent.task;

import org.dru.dusaf.event.EventBus;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface Task<T> extends EventBus {
    long getId();

    String getName();

    void cancel(boolean mayInterruptIfRunning);

    TaskState getState();

    long getCount();

    T get() throws InterruptedException, ExecutionException;

    T get(Duration timeout) throws InterruptedException, ExecutionException, TimeoutException;
}
