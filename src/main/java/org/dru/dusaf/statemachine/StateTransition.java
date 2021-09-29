package org.dru.dusaf.statemachine;

public interface StateTransition<E> {
    E getFrom();

    E getTo();
}
