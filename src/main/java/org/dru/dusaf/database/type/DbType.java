package org.dru.dusaf.database.type;

import java.sql.*;
import java.util.Collection;

public interface DbType<T> {
    Class<?>[] getJavaTypes();

    SQLType getSqlType();

    int getCapacity();

    String getDDL(Connection conn) throws SQLException;

    T get(ResultSet rset, int columnIndex) throws SQLException;

    void set(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;

    void set(PreparedStatement stmt, int parameterIndex, Collection<T> values) throws SQLException;

    void setNull(PreparedStatement stmt, int parameterIndex) throws SQLException;
}
