package org.dru.dusaf.functional;

import java.util.Objects;
import java.util.function.UnaryOperator;

import static org.dru.dusaf.functional.Throwing.raise;

@FunctionalInterface
public interface ThrowingUnaryOperator<T, E extends Throwable> extends ThrowingFunction<T, T, E> {
    static <T> UnaryOperator<T> wrap(final ThrowingUnaryOperator<T, ?> throwingUnaryOperator) {
        Objects.requireNonNull(throwingUnaryOperator, "throwingUnaryOperator");
        return t -> {
            try {
                return throwingUnaryOperator.apply(t);
            } catch (final Throwable throwable) {
                return raise(throwable);
            }
        };
    }
}
