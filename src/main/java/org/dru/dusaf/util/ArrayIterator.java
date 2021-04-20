package org.dru.dusaf.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class ArrayIterator<E> implements Iterator<E> {
    private E[] a;
    private int p;
    private int e;

    public ArrayIterator(final E[] array, final int offset, final int length) {
        Objects.requireNonNull(array, "array");
        if (offset < 0) {
            throw new IllegalArgumentException("negative offset");
        }
        if (length < 0) {
            throw new IllegalArgumentException("negative length");
        }
        if (offset + length > array.length) {
            throw new IllegalArgumentException("offset and length exceeds array length");
        }
        a = array;
        p = offset;
        e = p + length;
    }

    public ArrayIterator(final E[] array) {
        this(array, 0, array.length);
    }

    @Override
    public boolean hasNext() {
        return (p < e);
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return a[p++];
    }
}
