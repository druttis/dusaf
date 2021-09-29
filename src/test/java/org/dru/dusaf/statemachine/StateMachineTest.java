package org.dru.dusaf.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.dru.dusaf.statemachine.TestState.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateMachineTest {
    private StateMachine<TestState> stateMachine;

    @BeforeEach
    void setup() {
        final StateMachineSystem stateMachineSystem = new DefaultStateMachineSystem();
        stateMachine = stateMachineSystem.getStateMachine(TestTransition.class);
    }

    @Test
    void testValidStartState() {
        assertEquals(LOAD, stateMachine.getStartState());
    }

    @Test
    void testValidEndState() {
        assertEquals(EXIT, stateMachine.getEndState());
    }

    @Test
    void testInitialCurrentState() {
        assertEquals(stateMachine.getStartState(), stateMachine.getCurrentState());
    }

    @Test
    void testValidFlow() {
        stateMachine.gotoState(MENU);
        stateMachine.gotoState(DIORAMA);
        stateMachine.gotoState(MENU);
        stateMachine.gotoState(EXIT);
    }

    @Test
    void testInvalidFlow() {
        assertThrows(IllegalArgumentException.class, () -> {
            stateMachine.gotoState(MENU);
            stateMachine.gotoState(DIORAMA);
            stateMachine.gotoState(EXIT);
        });
    }
}