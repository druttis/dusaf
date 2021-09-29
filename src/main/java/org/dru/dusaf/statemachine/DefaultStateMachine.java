package org.dru.dusaf.statemachine;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class DefaultStateMachine<E> extends AbstractStateMachine<E> {
    private final Set<StateTransition<E>> allTransitions;
    private final Map<E, Set<E>> toStatesByState;
    private final Set<E> allStates;
    private final E startState;
    private final E endState;
    private final AtomicReference<E> currentStateRef;

    public DefaultStateMachine(final Set<StateTransition<E>> transitions) {
        Objects.requireNonNull(transitions, "transitions");
        allTransitions = Collections.unmodifiableSet(new HashSet<>(transitions));
        toStatesByState = createToStatesByState();
        allStates = createAllStates();
        startState = createStartState();
        endState = createEndState();
        currentStateRef = new AtomicReference<>(startState);
    }

    @Override
    public Collection<E> getAllStates() {
        return allStates;
    }

    @Override
    public E getStartState() {
        return startState;
    }

    @Override
    public E getEndState() {
        return endState;
    }

    @Override
    public E getCurrentState() {
        return currentStateRef.get();
    }

    @Override
    public void gotoState(final E state) {
        Objects.requireNonNull(state, "state");
        final E old = currentStateRef.getAndUpdate(current -> {
            if (!toStatesByState.getOrDefault(current, Collections.emptySet()).contains(state)) {
                throw new IllegalArgumentException("transitions from current state does not exist");
            }
            return state;
        });
        notifyStateObservers(old, state);
    }

    private Map<E, Set<E>> createToStatesByState() {
        final Map<E, Set<E>> toStatesByState = new HashMap<>();
        for (final StateTransition<E> transition : allTransitions) {
            toStatesByState.computeIfAbsent(transition.getFrom(), $ -> new HashSet<>()).add(transition.getTo());
        }
        return Collections.unmodifiableMap(toStatesByState.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.unmodifiableSet(e.getValue()))));
    }

    private Set<E> createAllStates() {
        final Set<E> allStates = new HashSet<>();
        for (final StateTransition<E> transition : allTransitions) {
            allStates.add(transition.getFrom());
            allStates.add(transition.getTo());
        }
        return Collections.unmodifiableSet(allStates);
    }

    private E createStartState() {
        final Set<E> startStates = new HashSet<>(allStates);
        allTransitions.forEach(t -> startStates.remove(t.getTo()));
        if (startStates.isEmpty()) {
            throw new IllegalArgumentException("start state does not exist");
        } else if (startStates.size() > 1) {
            throw new IllegalArgumentException("multiple start states exists");
        }
        return startStates.iterator().next();
    }

    private E createEndState() {
        final Set<E> endStates = new HashSet<>(allStates);
        allTransitions.forEach(t -> endStates.remove(t.getFrom()));
        if (endStates.isEmpty()) {
            throw new IllegalArgumentException("end state does not exist");
        } else if (endStates.size() > 1) {
            throw new IllegalArgumentException("multiple end states exists");
        }
        return endStates.iterator().next();
    }
}
