package org.dru.dusaf.database.type;

import org.dru.dusaf.database.dialect.DbDialects;

import java.sql.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public final class DialectalDbType<T> implements DbType<T> {
    private final DbDialects dialects;
    private final Class<T> javaType;
    private final int capacity;
    private final AtomicReference<DbType<T>> ref;

    public DialectalDbType(final DbDialects dialects, final Class<T> javaType, final int capacity) {
        this.dialects = dialects;
        this.javaType = javaType;
        this.capacity = capacity;
        ref = new AtomicReference<>();
    }

    @Override
    public Class<?>[] getJavaTypes() {
        return new Class<?>[]{javaType};
    }

    @Override
    public SQLType getSqlType() {
        return ref.get().getSqlType();
    }

    @Override
    public String getDDL(final Connection conn) throws SQLException {
        return get(conn).getDDL(conn);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public T get(final ResultSet rset, final int columnIndex) throws SQLException {
        return get(rset.getStatement().getConnection()).get(rset, columnIndex);
    }

    @Override
    public void set(final PreparedStatement stmt, final int parameterIndex, final T value) throws SQLException {
        get(stmt.getConnection()).set(stmt, parameterIndex, value);
    }

    @Override
    public void set(final PreparedStatement stmt, final int parameterIndex, final Collection<T> values)
            throws SQLException {
        get(stmt.getConnection()).set(stmt, parameterIndex, values);
    }

    @Override
    public void setNull(final PreparedStatement stmt, final int parameterIndex) throws SQLException {
        get(stmt.getConnection()).setNull(stmt, parameterIndex);
    }

    @SuppressWarnings("unchecked")
    private DbType<T> get(final Connection conn) throws SQLException {
        DbType<T> type = ref.get();
        if (type == null) {
            final String name = conn.getMetaData().getDatabaseProductName();
            type = dialects.get(name).getType((Class<T>) getJavaTypes()[0], getCapacity());
            if (!ref.compareAndSet(null, type)) {
                type = ref.get();
            }
        }
        return type;
    }
}
