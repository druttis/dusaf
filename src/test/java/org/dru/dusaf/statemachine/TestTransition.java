package org.dru.dusaf.statemachine;

import static org.dru.dusaf.statemachine.TestState.*;

public enum TestTransition implements StateTransition<TestState> {
    LOAD_TO_MENU(LOAD, MENU),
    MENU_TO_DIORAMA(MENU, DIORAMA),
    DIORAMA_TO_MENU(DIORAMA, MENU),
    MENU_TO_EXIT(MENU, EXIT);

    private final DefaultStateTransition<TestState> transition;

    TestTransition(final TestState from, final TestState to) {
        transition = new DefaultStateTransition<>(from, to);
    }

    @Override
    public TestState getFrom() {
        return transition.getFrom();
    }

    @Override
    public TestState getTo() {
        return transition.getTo();
    }
}
