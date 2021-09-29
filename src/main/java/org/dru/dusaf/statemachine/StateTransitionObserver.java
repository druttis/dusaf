package org.dru.dusaf.statemachine;

public interface StateTransitionObserver<E> {
    void stateTransitionPerformed(StateMachine<E> machine, E fromState, E toState);
}
