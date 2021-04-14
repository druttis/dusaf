package org.dru.dusaf.database.dialect;

import org.dru.dusaf.database.type.DbType;

public interface DbDialect {
    String getName();

    <T> DbType<T> getType(Class<T> javaType, int capacity);

    DbType<Boolean> getBooleanType();

    DbType<Byte> getByteType();

    DbType<Character> getCharType();

    DbType<Short> getShortType();

    DbType<Integer> getIntType();

    DbType<Long> getLongType();

    DbType<Float> getFloatType();

    DbType<Double> getDoubleType();

    DbType<String> getStringType(int capacity);                        // May return Blob<String> dep. on capacity

    DbType<boolean[]> getBoolArrayType(int capacity);                  // Blob<boolean[]>

    DbType<byte[]> getByteArrayType(int capacity);                     // Blob<byte[]>

    DbType<char[]> getCharArrayType(int capacity);                     // Blob<char[]>

    DbType<short[]> getShortArrayType(int capacity);                   // Blob<short[]>

    DbType<int[]> getIntArrayType(int capacity);                       // Blob<int[]>

    DbType<long[]> getLongArrayType(int capacity);                     // Blob<long[]>

    DbType<float[]> getFloatArrayType(int capacity);                   // Blob<float[]>

    DbType<double[]> getDoubleArrayType(int capacity);                 // Blob<double[]>

    DbType<String[]> getStringArrayType(int capacity);                 // Blob<String[]>

    <T> DbType<T> getJsonType(Class<T> type, int capacity);            // Blob<T>
}
