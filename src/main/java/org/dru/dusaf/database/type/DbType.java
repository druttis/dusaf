package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.function.Supplier;

public interface DbType<T> {
    SQLType getSQLType();

    boolean isVariableLength();

    T get(ResultSet rset, int columnIndex) throws SQLException;

    void set(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;

    String getDDL(int length);
}
