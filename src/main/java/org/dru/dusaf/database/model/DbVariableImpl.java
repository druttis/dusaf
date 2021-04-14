package org.dru.dusaf.database.model;

import org.dru.dusaf.database.type.DbType;
import org.dru.dusaf.database.type.DbTypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class DbVariableImpl<T> implements DbVariable<T> {
    private final Class<T> type;
    private final DbType<T> dbType;
    private final int capacity;

    public DbVariableImpl(final DbTypes dbtypes, final Class<T> type, final int capacity) {
        Objects.requireNonNull(dbtypes, "dbTypes");
        Objects.requireNonNull(type, "type");
        this.type = type;
        this.capacity = capacity;
        dbType = dbtypes.getType(type, getCapacity());
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public DbType<T> getDbType() {
        return dbType;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public T get(final ResultSet rset, final int columnIndex) throws SQLException {
        return dbType.get(rset, columnIndex);
    }

    @Override
    public void set(final PreparedStatement stmt, final int parameterIndex, final T value) throws SQLException {
        dbType.set(stmt, parameterIndex, value);
    }
}
