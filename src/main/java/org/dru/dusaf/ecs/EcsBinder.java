package org.dru.dusaf.ecs;

import org.dru.dusaf.util.Bits;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EcsBinder {
    private static Bits bind(final Bits bits, final Iterable<EcsMapping<?>> mappings) {
        final Bits result = (bits != null ? bits : new Bits());
        mappings.forEach(mapping -> result.set(mapping.getComponentId()));
        return result;
    }

    private static Bits bind(final Bits bits, final EcsMapping<?>... mappings) {
        return bind(bits, Arrays.asList(mappings));
    }

    private final EcsMappings mappings;
    Bits anyOf;
    Bits allOf;
    Bits noneOf;

    EcsBinder(final EcsMappings mappings) {
        this.mappings = mappings;
    }

    public EcsBinder bindAnyOf(final Class<?>... componentTypes) {
        anyOf = bind(anyOf, componentTypes);
        return this;
    }

    public EcsBinder bindAnyOf(final EcsMapping<?>... mappings) {
        anyOf = bind(anyOf, mappings);
        return this;
    }

    public EcsBinder bindAllOf(final Class<?>... componentTypes) {
        allOf = bind(allOf, componentTypes);
        return this;
    }

    public EcsBinder bindAllOf(final EcsMapping<?>... mappings) {
        allOf = bind(allOf, mappings);
        return this;
    }

    public EcsBinder bindNoneOf(final Class<?>... componentTypes) {
        noneOf = bind(noneOf, componentTypes);
        return this;
    }

    public EcsBinder bindNoneOf(final EcsMapping<?>... mappings) {
        noneOf = bind(noneOf, mappings);
        return this;
    }

    private Bits bind(final Bits bits, final Class<?>... componentsTypes) {
        return bind(bits, Stream.of(componentsTypes).map(mappings::getMapping).collect(Collectors.toList()));
    }
}
