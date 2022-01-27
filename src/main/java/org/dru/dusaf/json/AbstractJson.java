package org.dru.dusaf.json;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractJson implements Json {
    protected AbstractJson() {
    }

    @Override
    public final int size() {
        return isContainer() ? sizeImpl() : 0;
    }

    protected abstract int sizeImpl();

    @Override
    public final boolean has(final int index) {
        return isContainer() && hasImpl(index);
    }

    protected abstract boolean hasImpl(int index);

    @Override
    public final Json get(final int index) {
        return isContainer() ? getImpl(index) : null;
    }

    protected abstract Json getImpl(int index);

    protected abstract Json encode(final Object value);

    private void requireIsArray() {
        if (!isArray()) {
            throw new UnsupportedOperationException("not an array");
        }
    }

    @Override
    public final void add(final int index, final Object value) {
        requireIsArray();
        addImpl(index, encode(value));
    }

    @Override
    public final void add(final Object value) {
        requireIsArray();
        addImpl(sizeImpl(), encode(value));
    }

    protected abstract void addImpl(int index, Json elem);

    @Override
    public final Json set(final int index, final Object value) {
        requireIsArray();
        return setImpl(index, encode(value));
    }

    protected abstract Json setImpl(int index, Json elem);

    @Override
    public final Json remove(final int index) {
        requireIsArray();
        return removeImpl(index);
    }

    protected abstract Json removeImpl(int index);

    @Override
    public final Set<String> ids() {
        return isObject() ? idsImpl() : Collections.emptySet();
    }

    protected abstract Set<String> idsImpl();

    @Override
    public final boolean has(final String name) {
        return isObject() && hasImpl(name);
    }

    protected abstract boolean hasImpl(String name);

    @Override
    public final Json get(final String name) {
        return isObject() ? getImpl(name) : null;
    }

    protected abstract Json getImpl(String name);

    private void requireIsObject() {
        if (!isObject()) {
            throw new UnsupportedOperationException("not an object");
        }
    }

    @Override
    public final Json put(final String name, final Object value) {
        requireIsObject();
        return putImpl(name, encode(value));
    }

    protected abstract Json putImpl(String name, Json elem);

    @Override
    public final Json remove(final String name) {
        requireIsObject();
        return removeImpl(name);
    }

    protected abstract Json removeImpl(String name);

    @Override
    public final void clear() {
        if (!isContainer()) {
            throw new UnsupportedOperationException("not a container");
        }
        clearImpl();
    }

    protected abstract void clearImpl();
}

