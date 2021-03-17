package org.dru.dusaf.concurrent;

import org.dru.dusaf.functional.FloatBinaryOperator;
import org.dru.dusaf.functional.FloatUnaryOperator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;

public final class AtomicFloat extends Number implements Serializable {
    private static final long serialVersionUID = -455521020190422675L;

    private static float toFloat(final int value) {
        return intBitsToFloat(value);
    }

    private static int toInteger(final float value) {
        return floatToIntBits(value);
    }

    private final AtomicInteger bits;

    public AtomicFloat(final float value) {
        bits = new AtomicInteger(toInteger(value));
    }

    public AtomicFloat() {
        this(0f);
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
        return get();
    }

    @Override
    public double doubleValue() {
        return get();
    }

    @Override
    public String toString() {
        return Float.toString(get());
    }

    public float get() {
        return toFloat(bits.get());
    }

    public void set(final float value) {
        bits.set(toInteger(value));
    }

    public double getAndSet(final float value) {
        return toFloat(bits.getAndSet(toInteger(value)));
    }

    public boolean compareAndSet(final float expected, final float value) {
        return bits.compareAndSet(toInteger(expected), toInteger(value));
    }

    public boolean weakCompareAndSet(final float expected, final float value) {
        return bits.weakCompareAndSet(toInteger(expected), toInteger(value));
    }

    public float accumulateAndGet(final float value, final FloatBinaryOperator operator) {
        float prev, next;
        do {
            prev = get();
            next = operator.applyAsFloat(prev, value);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public float getAndAccumulate(final float value, final FloatBinaryOperator operator) {
        float prev, next;
        do {
            prev = get();
            next = operator.applyAsFloat(prev, value);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public float addAndGet(final float delta) {
        return toFloat(bits.addAndGet(toInteger(delta)));
    }

    public float incrementAndGet() {
        return addAndGet(1f);
    }

    public float subtractAndGet(final float delta) {
        return addAndGet(-delta);
    }

    public float decrementAndGet() {
        return subtractAndGet(1f);
    }

    public float getAndAdd(final float delta) {
        return toFloat(bits.getAndAdd(toInteger(delta)));
    }

    public float getAndIncrement() {
        return getAndAdd(1f);
    }

    public float getAndSubtract(final float delta) {
        return getAndAdd(-delta);
    }

    public float getAndDecrement() {
        return getAndSubtract(1f);
    }

    public float getAndUpdate(final FloatUnaryOperator operator) {
        float prev, next;
        do {
            prev = get();
            next = operator.applyAsFloat(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public float updateAndGet(final FloatUnaryOperator operator) {
        float prev, next;
        do {
            prev = get();
            next = operator.applyAsFloat(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public void lazySet(final float value) {
        bits.lazySet(toInteger(value));
    }
}
