package org.dru.dusaf.database.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DbColumn<T> extends DbObject, DbVariable<T> {
    int getLength();

    boolean isPrimary();

    boolean isNotNull();

    T get(ResultSet rset, int columnIndex) throws SQLException;

    void set(PreparedStatement stmt, int parameterIndex, T value) throws SQLException;
}
