package org.dru.dusaf.statemachine;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractStateMachine<E> implements StateMachine<E> {
    private final List<StateTransitionObserver<E>> transitionObservers;
    private final Map<E, List<StateObserver<E>>> stateObserversByState;
    private final List<StateObserver<E>> stateObservers;

    public AbstractStateMachine() {
        transitionObservers = new CopyOnWriteArrayList<>();
        stateObserversByState = new ConcurrentHashMap<>();
        stateObservers = new CopyOnWriteArrayList<>();
    }

    @Override
    public final void addStateTransitionObserver(final StateTransitionObserver<E> observer) {
        Objects.requireNonNull(observer, "observer");
        transitionObservers.add(observer);
    }

    @Override
    public final void removeStateTransitionObserver(final StateTransitionObserver<E> observer) {
        Objects.requireNonNull(observer, "observer");
        transitionObservers.remove(observer);
    }

    @Override
    public final void addStateObserver(final E state, final StateObserver<E> observer) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(observer, "observer");
        stateObserversByState.computeIfAbsent(state, $ -> new CopyOnWriteArrayList<>()).add(observer);
    }

    @Override
    public final void removeStateObserver(final E state, final StateObserver<E> observer) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(observer, "observer");
        stateObserversByState.computeIfPresent(state, ($, observers) -> {
            observers.remove(observer);
            return (observers.isEmpty() ? null : observers);
        });
    }

    @Override
    public final void addStateObserver(final StateObserver<E> observer) {
        Objects.requireNonNull(observer, "observer");
        stateObservers.add(observer);
    }

    @Override
    public final void removeStateObserver(final StateObserver<E> observer) {
        Objects.requireNonNull(observer, "observer");
        stateObservers.remove(observer);
    }

    protected final void notifyStateObservers(final E fromState, final E toState) {
        Objects.requireNonNull(fromState, "fromState");
        Objects.requireNonNull(toState, "toState");
        transitionObservers.forEach(observer -> observer.stateTransitionPerformed(this, fromState, toState));
        stateObserversByState.getOrDefault(fromState, Collections.emptyList())
                .forEach(observer -> observer.stateLeft(this, fromState));
        stateObservers.forEach(observer -> observer.stateLeft(this, fromState));
        stateObserversByState.getOrDefault(toState, Collections.emptyList())
                .forEach(observer -> observer.stateEntered(this, toState));
        stateObservers.forEach(observer -> observer.stateEntered(this, toState));
    }
}
