package org.dru.dusaf.database.dialect.mysql;

import org.dru.dusaf.database.dialect.DbDialect;
import org.dru.dusaf.database.type.*;
import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.serialization.*;

import java.sql.SQLType;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.mysql.cj.MysqlType.*;

public final class MysqlDialect implements DbDialect {
    private static final String NAME = "MySQL";

    private static final DbBoolType BOOL_TYPE = new DbBoolType(BIT, BIT.getPrecision().intValue());
    private static final DbByteType BYTE_TYPE = new DbByteType(TINYINT, TINYINT.getPrecision().intValue());
    private static final DbCharType CHAR_TYPE = new DbCharType(CHAR, 1);
    private static final DbShortType SHORT_TYPE = new DbShortType(SMALLINT, SMALLINT.getPrecision().intValue());
    private static final DbIntType INT_TYPE = new DbIntType(INT, INT.getPrecision().intValue());
    private static final DbLongType LONG_TYPE = new DbLongType(BIGINT, BIGINT.getPrecision().intValue());
    private static final DbFloatType FLOAT_TYPE = new DbFloatType(FLOAT, FLOAT.getPrecision().intValue());
    private static final DbDoubleType DOUBLE_TYPE = new DbDoubleType(DOUBLE, DOUBLE.getPrecision().intValue());

    private final JsonSerializer jsonSerializer;
    private final Map<Class<?>, Supplier<? extends DbType<?>>> supplierByType;
    private final Map<Class<?>, Function<Integer, ? extends DbType<?>>> functionByType;

    public MysqlDialect(final JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
        supplierByType = new ConcurrentHashMap<>();
        functionByType = new ConcurrentHashMap<>();
        registerSupplier(BOOL_TYPE, this::getBooleanType);
        registerSupplier(BYTE_TYPE, this::getByteType);
        registerSupplier(CHAR_TYPE, this::getCharType);
        registerSupplier(SHORT_TYPE, this::getShortType);
        registerSupplier(INT_TYPE, this::getIntType);
        registerSupplier(LONG_TYPE, this::getLongType);
        registerSupplier(FLOAT_TYPE, this::getFloatType);
        registerSupplier(DOUBLE_TYPE, this::getDoubleType);
        registerFunction(String.class, this::getStringType);
        registerFunction(boolean[].class, this::getBoolArrayType);
        registerFunction(byte[].class, this::getByteArrayType);
        registerFunction(char[].class, this::getCharArrayType);
        registerFunction(short[].class, this::getShortArrayType);
        registerFunction(int[].class, this::getIntArrayType);
        registerFunction(long[].class, this::getLongArrayType);
        registerFunction(float[].class, this::getFloatArrayType);
        registerFunction(double[].class, this::getDoubleArrayType);
        registerFunction(String[].class, this::getStringArrayType);
    }

    @SuppressWarnings("unchecked")
    private <T> void registerSupplier(final DbType<T> dbType, final Supplier<DbType<T>> supplier) {
        Stream.of(dbType.getJavaTypes()).forEach(javaType -> registerSupplier((Class<T>) javaType, supplier));
    }

    private <T> void registerSupplier(final Class<T> javaType, final Supplier<DbType<T>> supplier) {
        supplierByType.putIfAbsent(javaType, supplier);
    }

    @SuppressWarnings("unchecked")
    private <T> void registerFunction(final DbType<T> dbType, final Function<Integer, DbType<T>> function) {
        Stream.of(dbType.getJavaTypes()).forEach(javaType -> registerFunction((Class<T>) javaType, function));
    }

    @SuppressWarnings("unchecked")
    private <T> void registerFunction(final Class<? super T>[] javaTypes, final Function<Integer, DbType<T>> function) {
        Stream.of(javaTypes).forEach(javaType -> registerFunction((Class<T>) javaType, function));
    }


    private <T> void registerFunction(final Class<? super T> javaType, final Function<Integer, DbType<T>> function) {
        functionByType.putIfAbsent(javaType, function);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> DbType<T> getType(final Class<T> javaType, final int capacity) {
        Objects.requireNonNull(javaType, "type");
        final DbType<?> dbType;
        if (capacity < 0) {
            throw new IllegalArgumentException("negative capacity: " + capacity);
        } else if (capacity == 0) {
            final Supplier<? extends DbType<?>> supplier = supplierByType.get(javaType);
            if (supplier == null) {
                throw new IllegalArgumentException("type not registered: javaType=" + javaType.getName());
            }
            dbType = supplier.get();
        } else {
            final Function<Integer, ? extends DbType<?>> function = functionByType.get(javaType);
            if (function != null) {
                dbType = functionByType.get(javaType).apply(capacity);
            } else {
                dbType = getJsonType(javaType, capacity);
            }
        }
        if (dbType == null) {
            throw new IllegalArgumentException("type not registered: javaType=" + javaType.getName());
        }
        return (DbType<T>) dbType;
    }

    @Override
    public DbType<Boolean> getBooleanType() {
        return BOOL_TYPE;
    }

    @Override
    public DbType<Byte> getByteType() {
        return BYTE_TYPE;
    }

    @Override
    public DbType<Character> getCharType() {
        return CHAR_TYPE;
    }

    @Override
    public DbType<Short> getShortType() {
        return SHORT_TYPE;
    }

    @Override
    public DbType<Integer> getIntType() {
        return INT_TYPE;
    }

    @Override
    public DbType<Long> getLongType() {
        return LONG_TYPE;
    }

    @Override
    public DbType<Float> getFloatType() {
        return FLOAT_TYPE;
    }

    @Override
    public DbType<Double> getDoubleType() {
        return DOUBLE_TYPE;
    }

    @Override
    public DbType<String> getStringType(final int capacity) {
        if (capacity <= TINYTEXT.getPrecision()) {
            return new DbStringType(TINYTEXT, capacity);
        } else {
            return new DbBlobType<>(String.class, getBlobType(capacity), capacity, StringSerializer.INSTANCE);
        }
    }

    @Override
    public DbType<boolean[]> getBoolArrayType(final int capacity) {
        return new DbBlobType<>(boolean[].class, getBlobType(capacity), capacity, BoolArraySerializer.INSTANCE);
    }

    @Override
    public DbType<byte[]> getByteArrayType(final int capacity) {
        return new DbBlobType<>(byte[].class, getBlobType(capacity), capacity, ByteArraySerializer.INSTANCE);
    }

    @Override
    public DbType<char[]> getCharArrayType(final int capacity) {
        return new DbBlobType<>(char[].class, getBlobType(capacity), capacity, CharArraySerializer.INSTANCE);
    }

    @Override
    public DbType<short[]> getShortArrayType(final int capacity) {
        return new DbBlobType<>(short[].class, getBlobType(capacity), capacity, ShortArraySerializer.INSTANCE);
    }

    @Override
    public DbType<int[]> getIntArrayType(final int capacity) {
        return new DbBlobType<>(int[].class, getBlobType(capacity), capacity, IntArraySerializer.INSTANCE);
    }

    @Override
    public DbType<long[]> getLongArrayType(final int capacity) {
        return new DbBlobType<>(long[].class, getBlobType(capacity), capacity, LongArraySerializer.INSTANCE);
    }

    @Override
    public DbType<float[]> getFloatArrayType(final int capacity) {
        return new DbBlobType<>(float[].class, getBlobType(capacity), capacity, FloatArraySerializer.INSTANCE);
    }

    @Override
    public DbType<double[]> getDoubleArrayType(final int capacity) {
        return new DbBlobType<>(double[].class, getBlobType(capacity), capacity, DoubleArraySerializer.INSTANCE);
    }

    @Override
    public DbType<String[]> getStringArrayType(final int capacity) {
        return new DbBlobType<>(String[].class, getBlobType(capacity), capacity, StringArraySerializer.INSTANCE);
    }

    @Override
    public <T> DbType<T> getJsonType(final Class<T> type, final int capacity) {
        if (capacity <= TINYTEXT.getPrecision()) {
            return new DbBlobType<>(type, TINYTEXT, capacity,
                    new JsonDataSerializer<>(type, jsonSerializer));
        }
        return new DbBlobType<>(type, getBlobType(capacity), capacity,
                new JsonDataSerializer<>(type, jsonSerializer));
    }

    private SQLType getBlobType(final int capacity) {
        if (capacity <= TINYBLOB.getPrecision()) {
            return TINYBLOB;
        } else if (capacity <= BLOB.getPrecision()) {
            return BLOB;
        } else if (capacity <= MEDIUMBLOB.getPrecision()) {
            return MEDIUMBLOB;
        } else if (capacity <= LONGBLOB.getPrecision()) {
            return LONGBLOB;
        } else {
            throw new IllegalArgumentException("no blob type for capacity: " + capacity);
        }
    }
}
