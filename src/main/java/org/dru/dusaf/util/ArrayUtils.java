package org.dru.dusaf.util;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
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

    public static <E, K> int binarySearch(final E[] array, final int fromIndex, final int toIndex, final K key,
                                          final ToIntBiFunction<? super E, ? super K> comparator) {
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            final int mid = (low + high) >>> 1;
            final E midVal = array[mid];
            final int cmp = comparator.applyAsInt(midVal, key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found.
            }
        }
        return -(low + 1); // key not found, negative index of insertion.
    }

    private static int get(final byte[] buf, final int ofs) {
        return buf[ofs] & 0xff;
    }

    public static char getChar(final byte[] buf, final int ofs) {
        final int ch1 = get(buf, ofs);
        final int ch2 = get(buf, ofs + 1);
        return (char) ((ch1 << 8) | ch2);
    }

    public static short getShort(final byte[] buf, final int ofs) {
        final int ch1 = get(buf, ofs);
        final int ch2 = get(buf, ofs + 1);
        return (short) ((ch1 << 8) | ch2);
    }

    public static int getInt(final byte[] buf, final int ofs) {
        final int ch1 = get(buf, ofs);
        final int ch2 = get(buf, ofs + 1);
        final int ch3 = get(buf, ofs + 2);
        final int ch4 = get(buf, ofs + 3);
        return ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | ch4);
    }

    public static long getLong(final byte[] buf, final int ofs) {
        final long ch1 = get(buf, ofs);
        final long ch2 = get(buf, ofs + 1);
        final long ch3 = get(buf, ofs + 2);
        final long ch4 = get(buf, ofs + 3);
        final long ch5 = get(buf, ofs + 4);
        final long ch6 = get(buf, ofs + 5);
        final long ch7 = get(buf, ofs + 6);
        final long ch8 = get(buf, ofs + 7);
        return ((ch1 << 56) | (ch2 << 48) | (ch3 << 40) | (ch4 << 32) | (ch5 << 24) | (ch6 << 16) | (ch7 << 8) | ch8);
    }

    public static float getFloat(final byte[] buf, final int ofs) {
        return Float.intBitsToFloat(getInt(buf, ofs));
    }

    public static double getDouble(final byte[] buf, final int ofs) {
        return Double.longBitsToDouble(getLong(buf, ofs));
    }

    private static void put(final byte[] buf, final int ofs, final int val) {
        buf[ofs] = (byte) val;
    }

    public static void putChar(final byte[] buf, final int ofs, final char val) {
        put(buf, ofs, val >> 8);
        put(buf, ofs + 1, val);
    }

    public static void putShort(final byte[] buf, final int ofs, final short val) {
        put(buf, ofs, val >> 8);
        put(buf, ofs + 1, val);
    }

    public static void putInt(final byte[] buf, final int ofs, final int val) {
        put(buf, ofs, val >> 24);
        put(buf, ofs + 1, val >> 16);
        put(buf, ofs + 2, val >> 8);
        put(buf, ofs + 3, val);
    }

    public static void putLong(final byte[] buf, final int ofs, final long val) {
        put(buf, ofs, (int) (val >> 56));
        put(buf, ofs + 1, (int) (val >> 48));
        put(buf, ofs + 2, (int) (val >> 40));
        put(buf, ofs + 3, (int) (val >> 32));
        put(buf, ofs + 4, (int) (val >> 24));
        put(buf, ofs + 1, (int) (val >> 16));
        put(buf, ofs + 2, (int) (val >> 8));
        put(buf, ofs + 3, (int) val);
    }

    public static void putFloat(final byte[] buf, final int ofs, final float val) {
        putInt(buf, ofs, Float.floatToIntBits(val));
    }

    public static void putDouble(final byte[] buf, final int ofs, final double val) {
        putLong(buf, ofs, Double.doubleToLongBits(val));
    }

    public static int numBooleansInBytes(final int n) {
        return (n + 7) >> 3;
    }

    public static boolean[] getBooleans(final byte val, final boolean[] res, final int ofs, final int n) {
        final int m = Math.min(8, n);
        for (int i = 0; i < m; i++) {
            res[ofs + i] = (val & 1 << (7 - i)) != 0;
        }
        return res;
    }

    public static boolean[] getBooleans(final byte[] buf, final int src, final boolean[] res, final int dst,
                                        final int n) {
        for (int i = 0; i < n; i += 8) {
            getBooleans(buf[src + (i >> 3)], res, dst + i, n - i);
        }
        return res;
    }

    public static boolean[] getBooleans(final byte[] buf, final int ofs, final int n) {
        return getBooleans(buf, ofs, new boolean[n], 0, n);
    }

    public static byte getByte(final boolean[] buf, final int ofs, final int n) {
        final int m = Math.min(8, n);
        int v = 0;
        for (int i = 0; i < m; i++) {
            if (buf[ofs + i]) {
                v |= (1 << (7 - n));
            }
        }
        return (byte) v;
    }

    public static byte[] getBytes(final boolean[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i += 8) {
            res[dst + (i >> 3)] = getByte(buf, src + i, n - i);
        }
        return res;
    }

    public static byte[] getBytes(final boolean[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numBooleansInBytes(n)], 0, n);
    }

    public static int numCharsInBytes(final int n) {
        return n << 1;
    }

    public static char[] getChars(final byte[] buf, final int src, final char[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            res[dst + i] = getChar(buf, src + (i << 1));
        }
        return res;
    }

    public static char[] getChars(final byte[] buf, final int ofs, final int n) {
        return getChars(buf, ofs, new char[n], 0, n);
    }

    public static byte[] getBytes(final char[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            putChar(res, dst + (i << 1), buf[src + i]);
        }
        return res;
    }

    public static byte[] getBytes(final char[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numCharsInBytes(n)], 0, n);
    }

    public static int numShortsInBytes(final int n) {
        return n << 1;
    }

    public static short[] getShorts(final byte[] buf, final int src, final short[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            res[dst + i] = getShort(buf, src + (i << 1));
        }
        return res;
    }

    public static short[] getShorts(final byte[] buf, final int ofs, final int n) {
        return getShorts(buf, ofs, new short[n], 0, n);
    }

    public static byte[] getBytes(final short[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            putShort(res, dst + (i << 1), buf[src + i]);
        }
        return res;
    }

    public static byte[] getBytes(final short[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numShortsInBytes(n)], 0, n);
    }

    public static int numIntsInBytes(final int n) {
        return n << 2;
    }

    public static int[] getInts(final byte[] buf, final int src, final int[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            res[dst + i] = getInt(buf, src + (i << 2));
        }
        return res;
    }

    public static int[] getInts(final byte[] buf, final int ofs, final int n) {
        return getInts(buf, ofs, new int[n], 0, n);
    }

    public static byte[] getBytes(final int[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            putInt(res, dst + (i << 2), buf[src + i]);
        }
        return res;
    }

    public static byte[] getBytes(final int[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numIntsInBytes(n)], 0, n);
    }

    public static int numLongsInBytes(final int n) {
        return n << 3;
    }

    public static long[] getLongs(final byte[] buf, final int src, final long[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            res[dst + i] = getLong(buf, src + (i << 3));
        }
        return res;
    }

    public static long[] getLongs(final byte[] buf, final int ofs, final int n) {
        return getLongs(buf, ofs, new long[n], 0, n);
    }

    public static byte[] getBytes(final long[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            putLong(res, dst + (i << 3), buf[src + i]);
        }
        return res;
    }

    public static byte[] getBytes(final long[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numLongsInBytes(n)], 0, n);
    }

    public static int numFloatsInBytes(final int n) {
        return numIntsInBytes(n);
    }

    public static float[] getFloats(final byte[] buf, final int src, final float[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            res[dst + i] = getFloat(buf, src + (i << 2));
        }
        return res;
    }

    public static float[] getFloats(final byte[] buf, final int ofs, final int n) {
        return getFloats(buf, ofs, new float[n], 0, n);
    }

    public static byte[] getBytes(final float[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            putFloat(res, dst + (i << 2), buf[src + i]);
        }
        return res;
    }

    public static byte[] getBytes(final float[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numFloatsInBytes(n)], 0, n);
    }

    public static int numDoublesInBytes(final int n) {
        return numLongsInBytes(n);
    }

    public static double[] getDoubles(final byte[] buf, final int src, final double[] res, final int dst,
                                      final int n) {
        for (int i = 0; i < n; i++) {
            res[dst + i] = getDouble(buf, src + (i << 3));
        }
        return res;
    }

    public static double[] getDoubles(final byte[] buf, final int ofs, final int n) {
        return getDoubles(buf, ofs, new double[n], 0, n);
    }

    public static byte[] getBytes(final double[] buf, final int src, final byte[] res, final int dst, final int n) {
        for (int i = 0; i < n; i++) {
            putDouble(res, dst + (i << 3), buf[src + i]);
        }
        return res;
    }

    public static byte[] getBytes(final double[] buf, final int ofs, final int n) {
        return getBytes(buf, ofs, new byte[numDoublesInBytes(n)], 0, n);
    }

    public static int getVarIntLen(int x) {
        if ((x & (-1 << 7)) == 0) {
            return 1;
        } else if ((x & (-1 << 14)) == 0) {
            return 2;
        } else if ((x & (-1 << 21)) == 0) {
            return 3;
        } else if ((x & (-1 << 28)) == 0) {
            return 4;
        }
        return 5;
    }

    public static int getVarLongLen(long x) {
        int i = 1;
        while (true) {
            x >>>= 7;
            if (x == 0) {
                return i;
            }
            i++;
        }
    }

    public static int utf8Length(CharSequence sequence) {
        final int len = sequence.length();
        int count = 0;
        for (int index = 0; index < len; index++) {
            char ch = sequence.charAt(index);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                index++;
            } else {
                count += 3;
            }
        }
        return count;
    }
}
