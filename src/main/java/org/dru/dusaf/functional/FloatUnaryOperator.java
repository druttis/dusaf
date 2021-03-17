package org.dru.dusaf.functional;

import java.util.Objects;

@FunctionalInterface
public interface FloatUnaryOperator {
    float applyAsFloat(float operand);

    default FloatUnaryOperator compose(FloatUnaryOperator before) {
        Objects.requireNonNull(before);
        return (float v) -> applyAsFloat(before.applyAsFloat(v));
    }

    default FloatUnaryOperator andThen(FloatUnaryOperator after) {
        Objects.requireNonNull(after);
        return (float v) -> after.applyAsFloat(applyAsFloat(v));
    }

    static FloatUnaryOperator identity() {
        return t -> t;
    }
}
