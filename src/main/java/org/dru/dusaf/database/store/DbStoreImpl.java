package org.dru.dusaf.database.store;

import org.dru.dusaf.database.executor.DbExecutor;
import org.dru.dusaf.database.model.*;
import org.dru.dusaf.database.sql.SQL;
import org.dru.dusaf.database.sql.SQLSelectBuilder;
import org.dru.dusaf.functional.ThrowingBiConsumer;
import org.dru.dusaf.functional.ThrowingFunction;
import org.dru.dusaf.reflection.ReflectionUtils;
import org.dru.dusaf.time.TimeSupplier;
import org.dru.dusaf.util.JumpConsistentHash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public final class DbStoreImpl<K, V> implements DbStore<K, V> {
    private final DbExecutor dbExecutor;
    private final TimeSupplier timeSupplier;
    private final DbColumn<K> dbKey;
    private final DbColumn<V> dbValue;
    private final DbColumn<Long> dbCreated;
    private final DbColumn<Long> dbUpdated;
    private final DbTable dbTable;
    private final SQL selectSql;
    private final SQL selectForUpdateSql;
    private final SQL insertSql;
    private final SQL updateSql;
    private final SQL deleteSql;
    private final SQL deleteAllSql;

    DbStoreImpl(final DbExecutor dbExecutor, final TimeSupplier timeSupplier, final String name,
                final Class<K> keyType, final Class<V> valueType, final DbSystem tableBuilderFactory,
                final DbTableManager tableManager) {
        this.dbExecutor = dbExecutor;
        this.timeSupplier = timeSupplier;
        final DbTemplate tableBuilder = tableBuilderFactory.newTemplate(name);
        dbKey = tableBuilder.newColumn("key", keyType, DbModifier.PRIMARY);
        dbValue = tableBuilder.newColumn("value", valueType, 65535, DbModifier.NOT_NULL);
        dbCreated = tableBuilder.newColumn("created", Long.class, DbModifier.NOT_NULL);
        dbUpdated = tableBuilder.newColumn("updated", Long.class);
        dbTable = tableBuilder.build();
        tableManager.createTableIfNotExist(dbExecutor, 0, dbTable);
        selectSql = SQL.select(dbValue).from(dbTable).where(SQL.eq(dbKey)).limit(2).build();
        selectForUpdateSql = SQL.select(dbValue).from(dbTable).where(SQL.eq(dbKey)).limit(2).forUpdate().build();
        insertSql = SQL.insertInto(dbTable).columns(dbKey, dbValue, dbCreated).build();
        updateSql = SQL.update(dbTable).set(dbValue, dbUpdated).where(SQL.eq(dbKey)).build();
        deleteSql = SQL.deleteFrom(dbTable).where(SQL.eq(dbKey)).build();
        deleteAllSql = SQL.deleteFrom(dbTable).build();
    }

    @Override
    public V get(final K key) {
        try {
            int shardNum = JumpConsistentHash.hash(key, dbExecutor.getNumShards());
            V value = dbExecutor.query(shardNum, conn -> get(conn, key, false));
            if (value != null) {
                return value;
            }
            int[] shardNums = JumpConsistentHash.hashes(key, dbExecutor.getNumShards());
            for (int x : shardNums) {
                value = dbExecutor.query(x, conn -> get(conn, key, false));
                if (value != null) {
                    return value;
                }
            }
            return null;
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Map<K, V> get(final Set<K> keys) {
        try {
            return dbExecutor.query(0, conn -> get(conn, keys, false));
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Map<K, V> get(final int offset, final int limit) {
        try {
            return dbExecutor.query(0, conn -> get(conn, offset, limit));
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public V update(final K key, final UnaryOperator<V> operator) {
        try {
            return dbExecutor.update(0, conn -> {
                final V original = get(conn, key, true);
                final V result = operator.apply(ReflectionUtils.copyInstance(original));
                if (!Objects.equals(original, result)) {
                    if (original == null) {
                        insert(conn, key, result);
                    } else if (result != null) {
                        update(conn, key, result);
                    } else {
                        delete(conn, key);
                    }
                }
                return result;
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Map<K, V> update(final Set<K> keys, final UnaryOperator<V> operator) {
        try {
            return dbExecutor.update(0, conn -> {
                final Map<K, V> originals = get(conn, keys, true);
                final Map<K, V> inserts = new HashMap<>();
                final Map<K, V> updates = new HashMap<>();
                final Set<K> deletes = new HashSet<>();
                originals.forEach((key, original) -> {
                    final V result = operator.apply(ReflectionUtils.copyInstance(original));
                    if (!Objects.equals(original, result)) {
                        if (original == null) {
                            inserts.put(key, result);
                        } else if (result != null) {
                            updates.put(key, result);
                        } else {
                            deletes.add(key);
                        }
                    }
                });
                insert(conn, inserts);
                update(conn, updates);
                delete(conn, deletes);
                originals.putAll(inserts);
                originals.putAll(updates);
                originals.keySet().removeAll(deletes);
                return originals;
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Map<K, V> update(final Map<K, UnaryOperator<V>> operatorByKey) {
        try {
            return dbExecutor.update(0, conn -> {
                final Set<K> keys = operatorByKey.keySet();
                final Map<K, V> originals = get(conn, keys, true);
                final Map<K, V> inserts = new HashMap<>();
                final Map<K, V> updates = new HashMap<>();
                final Set<K> deletes = new HashSet<>();
                originals.forEach((key, original) -> {
                    final UnaryOperator<V> operator = operatorByKey.get(key);
                    final V result = operator.apply(ReflectionUtils.copyInstance(original));
                    if (!Objects.equals(original, result)) {
                        if (original == null) {
                            inserts.put(key, result);
                        } else if (result != null) {
                            updates.put(key, result);
                        } else {
                            deletes.add(key);
                        }
                    }
                });
                insert(conn, inserts);
                update(conn, updates);
                delete(conn, deletes);
                originals.putAll(inserts);
                originals.putAll(updates);
                originals.keySet().removeAll(deletes);
                return originals;
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Map<K, V> update(final Set<K> keys, final BiFunction<K, V, V> operator) {
        try {
            return dbExecutor.update(0, conn -> {
                final Map<K, V> originals = get(conn, keys, true);
                final Map<K, V> inserts = new HashMap<>();
                final Map<K, V> updates = new HashMap<>();
                final Set<K> deletes = new HashSet<>();
                originals.forEach((key, original) -> {
                    final V result = operator.apply(key, ReflectionUtils.copyInstance(original));
                    if (!Objects.equals(originals, result)) {
                        if (original == null) {
                            if (result != null) {
                                inserts.put(key, result);
                            }
                        } else if (result != null) {
                            updates.put(key, result);
                        } else {
                            deletes.add(key);
                        }
                    }
                });
                insert(conn, inserts);
                update(conn, updates);
                delete(conn, deletes);
                originals.putAll(inserts);
                originals.putAll(updates);
                originals.keySet().removeAll(deletes);
                return originals;
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public int delete(final K key) {
        try {
            return dbExecutor.update(0, conn -> {
                return delete(conn, key);
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public int delete(final Set<K> keys) {
        try {
            return dbExecutor.update(0, conn -> {
                return delete(conn, keys);
            });
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public int delete() {
        try {
            return dbExecutor.update(0, (ThrowingFunction<Connection, Integer, SQLException>) this::delete);
        } catch (final SQLException exc) {
            throw new RuntimeException(exc);
        }
    }

    private V get(final Connection conn, final K key, final boolean lock) throws SQLException {
        final SQL sql = (lock ? selectForUpdateSql : selectSql);
        try (final PreparedStatement stmt = sql.prepareStatement(conn)) {
            sql.set(stmt, dbKey, 0, key);
            try (final ResultSet rset = stmt.executeQuery()) {
                rset.setFetchSize(2);
                if (!rset.next()) {
                    return null;
                }
                final V result = sql.get(rset, dbValue);
                if (rset.next()) {
                    throw new SQLException("duplicate key: " + key);
                }
                return result;
            }
        }
    }

    private Map<K, V> get(final Connection conn, final Set<K> keys, final boolean lock) throws SQLException {
        final int size = keys.size();
        final SQLSelectBuilder builder
                = SQL.select(dbKey, dbValue).from(dbTable).where(SQL.in(dbKey, keys)).limit(size + 1);
        if (lock) {
            builder.forUpdate();
        }
        final SQL sql = builder.build();
        try (final PreparedStatement stmt = sql.prepareStatement(conn)) {
            sql.set(stmt, dbKey, 0, keys);
            try (final ResultSet rset = stmt.executeQuery()) {
                rset.setFetchSize(size + 1);
                final Map<K, V> result = new HashMap<>();
                while (rset.next()) {
                    K key = sql.get(rset, dbKey);
                    V value = sql.get(rset, dbValue);
                    if (result.put(key, value) != null) {
                        throw new SQLException("duplicate key: " + key);
                    }
                }
                return result;
            }
        }
    }

    private Map<K, V> get(final Connection conn, final int offset, final int limit)
            throws SQLException {
        final SQL sql = SQL.select(dbKey, dbValue).from(dbTable).offset(offset).limit(limit).build();
        try (final PreparedStatement stmt = sql.prepareStatement(conn)) {
            try (final ResultSet rset = stmt.executeQuery()) {
                rset.setFetchSize(limit);
                final Map<K, V> result = new HashMap<>();
                while (rset.next()) {
                    K key = sql.get(rset, dbKey);
                    V value = sql.get(rset, dbValue);
                    if (result.put(key, value) != null) {
                        throw new SQLException("duplicate key: " + key);
                    }
                }
                return result;
            }
        }
    }

    private void insert(final Connection conn, final K key, final V value) throws SQLException {
        try (final PreparedStatement stmt = insertSql.prepareStatement(conn)) {
            insertSql.set(stmt, dbKey, 0, key);
            insertSql.set(stmt, dbValue, 0, value);
            insertSql.set(stmt, dbCreated, 0, timeSupplier.get().toEpochMilli());
            stmt.executeUpdate();
        }
    }

    private void insert(final Connection conn, final Map<K, V> valueByKey) throws SQLException {
        try (final PreparedStatement stmt = insertSql.prepareStatement(conn)) {
            valueByKey.forEach(ThrowingBiConsumer.wrap((ThrowingBiConsumer<K, V, SQLException>) (key, value) -> {
                insertSql.set(stmt, dbKey, 0, key);
                insertSql.set(stmt, dbValue, 0, value);
                insertSql.set(stmt, dbCreated, 0, timeSupplier.get().toEpochMilli());
                stmt.addBatch();
            }));
            stmt.executeBatch();
        }
    }

    private void update(final Connection conn, final K key, final V value) throws SQLException {
        try (final PreparedStatement stmt = updateSql.prepareStatement(conn)) {
            updateSql.set(stmt, dbValue, 0, value);
            updateSql.set(stmt, dbUpdated, 0, timeSupplier.get().toEpochMilli());
            updateSql.set(stmt, dbKey, 0, key);
            final int result = stmt.executeUpdate();
            if (result > 1) {
                throw new SQLException("duplicate key: " + key);
            }
        }
    }

    private void update(final Connection conn, final Map<K, V> valueByKey) throws SQLException {
        try (final PreparedStatement stmt = updateSql.prepareStatement(conn)) {
            valueByKey.forEach(ThrowingBiConsumer.wrap((ThrowingBiConsumer<K, V, SQLException>) (key, value) -> {
                updateSql.set(stmt, dbValue, 0, value);
                updateSql.set(stmt, dbUpdated, 0, timeSupplier.get().toEpochMilli());
                updateSql.set(stmt, dbKey, 0, key);
                stmt.addBatch();
            }));
            final int result = IntStream.of(stmt.executeBatch()).sum();
            if (result > valueByKey.size()) {
                throw new SQLException("duplicate key");
            }
        }
    }

    private int delete(final Connection conn, final K key) throws SQLException {
        try (final PreparedStatement stmt = deleteSql.prepareStatement(conn)) {
            deleteSql.set(stmt, dbKey, 0, key);
            final int result = stmt.executeUpdate();
            if (result > 1) {
                throw new SQLException("duplicate key: " + key);
            }
            return result;
        }
    }

    private int delete(final Connection conn, final Set<K> keys) throws SQLException {
        final SQL sql = SQL.deleteFrom(dbTable).where(SQL.in(dbKey, keys)).build();
        try (final PreparedStatement stmt = sql.prepareStatement(conn)) {
            sql.set(stmt, dbKey, 0, keys);
            final int result = stmt.executeUpdate();
            if (result > keys.size()) {
                throw new SQLException("duplicate key(s)");
            }
            return result;
        }
    }

    private int delete(final Connection conn) throws SQLException {
        try (final PreparedStatement stmt = deleteAllSql.prepareStatement(conn)) {
            return stmt.executeUpdate();
        }
    }
}
