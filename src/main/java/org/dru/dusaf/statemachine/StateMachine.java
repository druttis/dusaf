package org.dru.dusaf.statemachine;

import java.util.Collection;

public interface StateMachine<E> {
    void addStateTransitionObserver(StateTransitionObserver<E> observer);

    void removeStateTransitionObserver(StateTransitionObserver<E> observer);

    void addStateObserver(E state, StateObserver<E> observer);

    void removeStateObserver(E state, StateObserver<E> observer);

    void addStateObserver(StateObserver<E> observer);

    void removeStateObserver(StateObserver<E> observer);

    Collection<E> getAllStates();

    E getStartState();

    E getEndState();

    E getCurrentState();

    void gotoState(E state);
}
