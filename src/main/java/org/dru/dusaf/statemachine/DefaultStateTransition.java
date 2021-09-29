package org.dru.dusaf.statemachine;

import java.util.Objects;

public final class DefaultStateTransition<E> implements StateTransition<E> {
    private final E from;
    private final E to;

    public DefaultStateTransition(final E from, final E to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        this.from = from;
        this.to = to;
    }

    @Override
    public E getFrom() {
        return from;
    }

    @Override
    public E getTo() {
        return to;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultStateTransition<?> that = (DefaultStateTransition<?>) o;
        return from.equals(that.from) && to.equals(that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "DefaultStateTransition{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
