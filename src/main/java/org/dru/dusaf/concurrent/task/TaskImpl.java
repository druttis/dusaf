package org.dru.dusaf.concurrent.task;

import org.dru.dusaf.event.DefaultEventBus;
import org.dru.dusaf.event.EventBus;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class TaskImpl<T> extends DefaultEventBus implements Task<T> {
    private final long id;
    private final String name;
    private final AtomicReference<TaskState> stateRef;
    private final AtomicLong count;
    private Future<T> future;

    TaskImpl(final EventBus eventBus, final long id, final String name) {
        super(eventBus);
        this.id = id;
        this.name = name;
        stateRef = new AtomicReference<>(TaskState.CREATED);
        count = new AtomicLong();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void cancel(final boolean mayInterruptIfRunning) {
        future.cancel(mayInterruptIfRunning);
    }

    @Override
    public TaskState getState() {
        return stateRef.get();
    }

    @Override
    public long getCount() {
        return count.get();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public T get(final Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout.toNanos(), TimeUnit.NANOSECONDS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void setFuture(final Future future) {
        this.future = future;
    }

    void incrementCount() {
        count.incrementAndGet();
        fireEvent(new TaskStateChangeEvent(this));
    }

    void setState(final TaskState state) {
        Objects.requireNonNull(state, "state");
        final TaskState old = stateRef.getAndUpdate($ -> state);
        if (state != old) {
            fireEvent(new TaskStateChangeEvent(this));
        }
    }
}
