package org.dru.dusaf.statemachine;

public interface StateMachineBuilder<E> {
    StateMachineBuilder<E> withTransition(E from, E to);

    StateMachine<E> build();
}
