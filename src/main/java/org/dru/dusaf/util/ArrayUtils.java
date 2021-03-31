package org.dru.dusaf.util;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class ArrayUtils {
    public static <E> int indexOf(final E[] array, Predicate<? super E> filter, final int offset, final int length) {
        return IntStream.range(offset, length).filter(index -> filter.test(array[index])).findFirst().orElse(-1);
    }

    public static <E> int indexOf(final E[] array, Predicate<? super E> filter) {
        return indexOf(array, filter, 0, array.length);
    }

    public static <E> E[] add(final E[] array, final E elem, final int index) {
        final int length = array.length;
        final E[] newArray = Arrays.copyOf(array, length + 1);
        newArray[length] = elem;
        return newArray;
    }

    public static <E> E[] add(final E[] array, final E elem) {
        return add(array, elem, array.length);
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] remove(final E[] array, final int index) {
        final int newLength = array.length - 1;
        final E[] newArray = (E[]) new Object[newLength];
        System.arraycopy(array, 0, newArray, 0, index);
        if (index < newLength) {
            System.arraycopy(array, index + 1, newArray, index, newLength - index);
        }
        return newArray;
    }
}
