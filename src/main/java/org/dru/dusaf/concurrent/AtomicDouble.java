package org.dru.dusaf.concurrent;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;

public final class AtomicDouble extends Number implements Serializable {
    private static final long serialVersionUID = -455521020190422675L;

    private static double toDouble(final long value) {
        return longBitsToDouble(value);
    }

    private static long toLong(final double value) {
        return doubleToLongBits(value);
    }

    private final AtomicLong bits;

    public AtomicDouble(final double value) {
        bits = new AtomicLong(toLong(value));
    }

    public AtomicDouble() {
        this(0d);
    }

    @Override
    public int intValue() {
        return (int) get();
    }

    @Override
    public long longValue() {
        return (int) get();
    }

    @Override
    public float floatValue() {
        return (float) get();
    }

    @Override
    public double doubleValue() {
        return get();
    }

    @Override
    public String toString() {
        return Double.toString(get());
    }

    public double get() {
        return toDouble(bits.get());
    }

    public void set(final double value) {
        bits.set(toLong(value));
    }

    public double getAndSet(final double value) {
        return toDouble(bits.getAndSet(toLong(value)));
    }

    public boolean compareAndSet(final double expected, final double value) {
        return bits.compareAndSet(toLong(expected), toLong(value));
    }

    public boolean weakCompareAndSet(final double expected, final double value) {
        return bits.weakCompareAndSet(toLong(expected), toLong(value));
    }

    public double accumulateAndGet(final double value, final DoubleBinaryOperator operator) {
        double prev, next;
        do {
            prev = get();
            next = operator.applyAsDouble(prev, value);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public double getAndAccumulate(final double value, final DoubleBinaryOperator operator) {
        double prev, next;
        do {
            prev = get();
            next = operator.applyAsDouble(prev, value);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public double addAndGet(final double delta) {
        return toDouble(bits.addAndGet(toLong(delta)));
    }

    public double incrementAndGet() {
        return addAndGet(1d);
    }

    public double subtractAndGet(final double delta) {
        return addAndGet(-delta);
    }

    public double decrementAndGet() {
        return subtractAndGet(1d);
    }

    public double getAndAdd(final double delta) {
        return toDouble(bits.getAndAdd(toLong(delta)));
    }

    public double getAndIncrement() {
        return getAndAdd(1d);
    }

    public double getAndSubtract(final double delta) {
        return getAndAdd(-delta);
    }

    public double getAndDecrement() {
        return getAndSubtract(1d);
    }

    public double getAndUpdate(final DoubleUnaryOperator operator) {
        double prev, next;
        do {
            prev = get();
            next = operator.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public double updateAndGet(final DoubleUnaryOperator operator) {
        double prev, next;
        do {
            prev = get();
            next = operator.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public void lazySet(final double value) {
        bits.lazySet(toLong(value));
    }
}
