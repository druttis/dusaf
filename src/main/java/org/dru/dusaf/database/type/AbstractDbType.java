package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Objects;

public abstract class AbstractDbType<T> implements DbType<T> {
    private final Class<T> type;
    private final SQLType sqlType;
    private final boolean variableLength;
    private final int minLength;
    private final int maxLength;

    protected AbstractDbType(final Class<T> type, final SQLType sqlType, final boolean variableLength,
                             final int minLength, final int maxLength) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(sqlType, "sqlType");
        if (variableLength) {
            if (minLength < 1) {
                throw new IllegalArgumentException("minLength has to be 1 or greater: " + minLength);
            }
            if (maxLength < minLength) {
                throw new IllegalArgumentException("maxLength has to be greater or equal to minLength: " +
                        "maxLength=" + maxLength + ", minLength=" + minLength);
            }
        } else{
            if (minLength != 0) {
                throw new IllegalArgumentException("minLength has to be 0: " + minLength);
            }
            if (maxLength != 0) {
                throw new IllegalArgumentException("maxLength has to be 0: " + maxLength);
            }
        }
        this.type = type;
        this.sqlType = sqlType;
        this.variableLength = variableLength;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public final SQLType getSqlType() {
        return sqlType;
    }

    @Override
    public final boolean isVariableLength() {
        return variableLength;
    }

    @Override
    public final int getMinLength() {
        return minLength;
    }

    @Override
    public final int getMaxLength() {
        return maxLength;
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
            stmt.setNull(parameterIndex, getSqlType().getVendorTypeNumber());
        }
    }

    protected abstract T doGet(ResultSet rset, int columnIndex) throws SQLException;

    protected abstract void doSet(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;
}
