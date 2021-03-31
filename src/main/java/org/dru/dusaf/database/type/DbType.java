package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public interface DbType<T> {
    static boolean contains(DbType<?> type, int length) {
        return (type.getMinLength() <= length && length <= type.getMaxLength());
    }

    static boolean intersects(DbType<?> a, DbType<?> b) {
        return (b.getMaxLength() >= a.getMinLength() && a.getMaxLength() >= b.getMinLength());
    }

    Class<T> getType();

    SQLType getSqlType();

    boolean isVariableLength();

    int getMinLength();

    int getMaxLength();

    T get(ResultSet rset, int columnIndex) throws SQLException;

    void set(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;
}
