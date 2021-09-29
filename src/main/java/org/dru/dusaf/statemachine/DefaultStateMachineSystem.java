package org.dru.dusaf.statemachine;

import org.dru.dusaf.reflection.ReflectionUtils;

public final class DefaultStateMachineSystem implements StateMachineSystem {
    public DefaultStateMachineSystem() {
    }

    @Override
    public <T extends Enum<T> & StateTransition<E>, E extends Enum<E>> StateMachine<E>
    getStateMachine(final Class<T> type) {
        final StateMachineBuilder<E> stateMachineBuilder = getStateMachineBuilder();
        for (final T constant : ReflectionUtils.getEnumConstants(type)) {
            stateMachineBuilder.withTransition(constant.getFrom(), constant.getTo());
        }
        return stateMachineBuilder.build();
    }

    @Override
    public <E> StateMachineBuilder<E> getStateMachineBuilder() {
        return new DefaultStateMachineBuilder<>();
    }
}
