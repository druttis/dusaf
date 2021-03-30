package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Objects;

public abstract class AbstractDbType<T> implements DbType<T> {
    private final SQLType sqlType;
    private final boolean variableLength;

    protected AbstractDbType(final SQLType sqlType, final boolean variableLength) {
        this.sqlType = Objects.requireNonNull(sqlType, "sqlType");
        this.variableLength = variableLength;
    }

    protected AbstractDbType(final SQLType sqlType) {
        this(sqlType, false);
    }

    @Override
    public final SQLType getSQLType() {
        return sqlType;
    }

    @Override
    public final boolean isVariableLength() {
        return variableLength;
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
            stmt.setNull(parameterIndex, getSQLType().getVendorTypeNumber());
        }
    }

    @Override
    public final String getDDL(final int length) {
        if (isVariableLength()) {
            return String.format("%s(%d)", getSQLType().getName(), length);
        } else {
            return getSQLType().getName();
        }
    }

    @Override
    public String toString() {
        return sqlType.getName() + (variableLength ? "(n)" : "");
    }

    protected abstract T doGet(ResultSet rset, int columnIndex) throws SQLException;

    protected abstract void doSet(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;
}
