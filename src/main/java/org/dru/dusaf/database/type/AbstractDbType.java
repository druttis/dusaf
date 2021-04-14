package org.dru.dusaf.database.type;

import org.dru.dusaf.database.sql.SQLCondition;

import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class AbstractDbType<T> implements DbType<T> {
    private final Class<?>[] javaTypes;
    private final SQLType sqlType;
    private final int capacity;

    protected static Class<?>[] array(final Class<?>... cl) {
        return cl;
    }

    public AbstractDbType(final Class<?>[] javaTypes, final SQLType sqlType, final int capacity) {
        Objects.requireNonNull(javaTypes, "javaTypes");
        this.javaTypes = javaTypes;
        this.sqlType = sqlType;
        this.capacity = capacity;
    }

    public AbstractDbType(final Class<?> javaType, final SQLType sqlType, final int capacity) {
        this(array(javaType), sqlType, capacity);
    }


    @Override
    public final Class<?>[] getJavaTypes() {
        return Arrays.copyOf(javaTypes, javaTypes.length);
    }

    @Override
    public final SQLType getSqlType() {
        return sqlType;
    }

    @Override
    public final int getCapacity() {
        return capacity;
    }

    @Override
    public String getDDL(final Connection conn) throws SQLException {
        return getSqlType().getName();
    }

    @Override
    public final T get(final ResultSet rset, final int columnIndex) throws SQLException {
        Objects.requireNonNull(rset, "rset");
        return doGet(rset, columnIndex);
    }

    @Override
    public final void set(final PreparedStatement stmt, final int parameterIndex, final T value) throws SQLException {
        Objects.requireNonNull(stmt, "stmt");
        if (value != null) {
            doSet(stmt, parameterIndex, value);
        } else {
            doSetNull(stmt, parameterIndex);
        }
    }

    @Override
    public final void set(final PreparedStatement stmt, final int parameterIndex, final Collection<T> values)
            throws SQLException {
        Objects.requireNonNull(stmt, "stmt");
        if (values != null) {
            doSet(stmt, parameterIndex, values);
        } else {
            doSetNull(stmt, parameterIndex);
        }
    }

    @Override
    public final void setNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
        Objects.requireNonNull(stmt, "stmt");
        doSetNull(stmt, parameterIndex);
    }

    protected void doSet(PreparedStatement stmt, int parameterIndex, Collection<T> values) throws SQLException {
        final Array array = stmt.getConnection().createArrayOf(getSqlType().getName(), values.toArray());
        stmt.setArray(parameterIndex, array);
    }

    protected abstract T doGet(ResultSet rset, int columnIndex) throws SQLException;

    protected abstract void doSet(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;

    private void doSetNull(final PreparedStatement stmt, final int parameterIndex) throws SQLException {
        stmt.setNull(parameterIndex, getSqlType().getVendorTypeNumber());
    }
}
