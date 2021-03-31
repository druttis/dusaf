package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;
import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.json.JsonSerializerSupplier;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DbTypesImpl implements DbTypes {
    private final Map<TypeLength<?>, DbType<?>> dbTypeByTypeLength;
    private final Map<Class<?>, Collection<DbType<?>>> dbTypesByJavaType;

    private final JsonSerializer jsonSerializer;

    public DbTypesImpl(final JsonSerializerSupplier jsonSerializerSupplier) {
        jsonSerializer = jsonSerializerSupplier.get();
        dbTypeByTypeLength = new ConcurrentHashMap<>();
        dbTypesByJavaType = new ConcurrentHashMap<>();
        registerDefaults();
    }

    private void registerDefaults() {
        register(DbBoolean.BOXED);
        register(DbBoolean.PRIMITIVE);
        register(DbByte.BOXED);
        register(DbByte.PRIMITIVE);
        register(DbByteArray.TINY);
        register(DbByteArray.SMALL);
        register(DbByteArray.MEDIUM);
        register(DbByteArray.LONG);
        register(DbCharacter.BOXED);
        register(DbCharacter.PRIMITIVE);
        register(DbDouble.BOXED);
        register(DbDouble.PRIMITIVE);
        register(DbFloat.BOXED);
        register(DbFloat.PRIMITIVE);
        register(DbInteger.BOXED);
        register(DbInteger.PRIMITIVE);
        register(DbLong.BOXED);
        register(DbLong.PRIMITIVE);
        register(DbShort.BOXED);
        register(DbShort.PRIMITIVE);
        register(DbString.TINY);
        register(DbString.SMALL);
        register(DbString.MEDIUM);
        register(DbString.LONG);
    }

    public <T> void register(final DbType<T> dbType) {
        Objects.requireNonNull(dbType, "dbType");
        dbTypesByJavaType.compute(dbType.getType(), ($, dbTypes) -> {
            if (dbTypes == null) {
                dbTypes = new CopyOnWriteArrayList<>();
            }
            final Optional<DbType<?>> intersects = dbTypes.stream()
                    .filter(b -> DbType.intersects(dbType, b)).findFirst();
            if (intersects.isPresent()) {
                throw new IllegalArgumentException("range collision");
            }
            dbTypes.add(dbType);
            return dbTypes;
        });

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> DbType<T> of(final Class<T> type, final int length) {
        Objects.requireNonNull(type, "type");
        if (length < 0) {
            throw new IllegalArgumentException("negative length: " + length);
        }
        final TypeLength<T> typeLength = new TypeLength<>(type, length);
        return (DbType<T>) dbTypeByTypeLength.computeIfAbsent(typeLength, $ -> {
            final Collection<DbType<?>> dbTypes = dbTypesByJavaType.get(type);
            final Optional<DbType<?>> result = dbTypes.stream()
                    .filter(dbType -> DbType.contains(dbType, length))
                    .findFirst();
            if (result.isPresent()) {
                return result.get();
            }
            if (length < 256) {
                return new DbJson<>(type, MysqlType.TINYBLOB, 1, 255, jsonSerializer);
            } else if (length < 65536) {
                return new DbJson<>(type, MysqlType.TINYBLOB, 256, 65535, jsonSerializer);
            } else if (length < 16777216) {
                return new DbJson<>(type, MysqlType.TINYBLOB, 65535, 16777215, jsonSerializer);
            } else {
                return new DbJson<>(type, MysqlType.TINYBLOB, 16777216, Integer.MAX_VALUE, jsonSerializer);
            }
        });
    }

    private static class TypeLength<T> {
        private final Class<T> type;
        private final int length;

        private TypeLength(final Class<T> type, final int length) {
            this.type = type;
            this.length = length;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final TypeLength<?> that = (TypeLength<?>) o;
            return length == that.length && type.equals(that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, length);
        }
    }
}
