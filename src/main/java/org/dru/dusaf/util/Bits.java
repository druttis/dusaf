package org.dru.dusaf.util;

import java.util.Arrays;

public class Bits {
    private long[] array = {};

    public Bits(final int nbits) {
        ensureCapacity(nbits >>> 6);
    }

    public Bits() {
    }

    public int size() {
        return array.length;
    }

    public boolean get(final int bit) {
        if (bit < 0) {
            throw new ArrayIndexOutOfBoundsException(bit);
        }
        final int index = bit >>> 6;
        return (index < size() && (array[index] & (1L << (bit & 63))) != 0L);
    }

    public void set(final int bit) {
        final int index = bit >>> 6;
        ensureCapacity(index);
        array[index] |= 1L << (bit & 0x63);
    }

    public void clear(final int bit) {
        if (bit < 0) {
            throw new ArrayIndexOutOfBoundsException(bit);
        }
        final int index = bit >>> 6;
        if (index < size()) {
            array[index] &= ~(1L << (bit & 63));
        }
    }

    public void clear() {
        Arrays.fill(array, 0L);
    }

    public boolean intersects(final Bits other) {
        long[] bits = this.array;
        long[] otherBits = other.array;
        final int length = Math.min(bits.length, otherBits.length);
        for (int index = 0; index < length; index++) {
            if ((bits[index] & otherBits[index]) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(final Bits other) {
        long[] bits = this.array;
        long[] otherBits = other.array;
        int otherBitsLength = otherBits.length;
        int bitsLength = bits.length;
        for (int index = bitsLength; index < otherBitsLength; index++) {
            if (otherBits[index] != 0) {
                return false;
            }
        }
        final int length = Math.min(bitsLength, otherBitsLength);
        for (int index = 0; index < length; index++) {
            if ((bits[index] & otherBits[index]) != otherBits[index]) {
                return false;
            }
        }
        return true;
    }

    public int length() {
        final long[] array = this.array;
        for (int index = array.length - 1; index >= 0; --index) {
            final long bits = array[index];
            if (bits != 0) {
                for (int bit = 63; bit >= 0; --bit) {
                    if ((bits & (1L << (bit & 63))) != 0L) {
                        return (index << 6) + bit + 1;
                    }
                }
            }
        }
        return 0;
    }

    public boolean isEmpty() {
        final long[] array = this.array;
        int size = size();
        for (int index = 0; index < size; index++) {
            if (array[index] != 0L) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int size = size();
        int hash = 0;
        for (int index = 0; index < size; index++) {
            hash = 127 * hash + (int) (array[index] ^ (array[index] >>> 32));
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        final Bits other = (Bits) obj;
        final long[] array = this.array;
        final long[] otherArray = other.array;
        final int commonSize = Math.min(size(), other.size());
        for (int index = 0; index < commonSize; index++) {
            if (array[index] != otherArray[index])
                return false;
        }
        if (size() == other.size()) {
            return true;
        }
        return length() == other.length();
    }

    private void ensureCapacity(final int size) {
        if (size >= size()) {
            array = Arrays.copyOf(array, size + 1);
        }
    }
}
