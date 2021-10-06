package org.dru.dusaf.ecs;

import org.dru.dusaf.util.Bits;

import java.util.Objects;

final class EcsAspect {
    private final Bits anyOf;
    private final Bits allOf;
    private final Bits noneOf;

    EcsAspect(final Bits anyOf, final Bits allOf, final Bits noneOf) {
        this.anyOf = anyOf;
        this.allOf = allOf;
        this.noneOf = noneOf;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EcsAspect family = (EcsAspect) o;
        return anyOf.equals(family.anyOf) && allOf.equals(family.allOf) && noneOf.equals(family.noneOf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anyOf, allOf, noneOf);
    }

    boolean test(final Bits bits) {
        return (anyOf == null || anyOf.intersects(bits))
                && (allOf == null || allOf.containsAll(bits))
                && (noneOf == null || !noneOf.intersects(bits));
    }
}
