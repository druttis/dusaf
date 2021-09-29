package org.dru.dusaf.statemachine;

public interface StateMachineSystem {
    <T extends Enum<T> & StateTransition<E>, E extends Enum<E>> StateMachine<E> getStateMachine(Class<T> type);

    <E> StateMachineBuilder<E> getStateMachineBuilder();
}
