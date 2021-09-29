package org.dru.dusaf.statemachine;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultStateMachineBuilder<E> implements StateMachineBuilder<E> {
    private final Set<StateTransition<E>> transitions;

    public DefaultStateMachineBuilder() {
        transitions = ConcurrentHashMap.newKeySet();
    }

    @Override
    public StateMachineBuilder<E> withTransition(final E from, final E to) {
        transitions.add(new DefaultStateTransition<>(from, to));
        return this;
    }

    @Override
    public StateMachine<E> build() {
        return new DefaultStateMachine<>(transitions);
    }
}
