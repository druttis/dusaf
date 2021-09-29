package org.dru.dusaf.statemachine;

public interface StateObserver<E> {
    void stateEntered(StateMachine<E> machine, E state);

    void stateLeft(StateMachine<E> machine, E state);
}
