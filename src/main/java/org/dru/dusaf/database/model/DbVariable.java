package org.dru.dusaf.database.model;

import org.dru.dusaf.database.type.DbType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DbVariable<T> {
    Class<T> getType();

    DbType<T> getDbType();

    T get(ResultSet rset, int columnIndex) throws SQLException;

    void set(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;
}
