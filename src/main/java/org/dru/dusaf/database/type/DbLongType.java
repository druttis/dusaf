package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbLongType extends AbstractDbType<Long> {
    public DbLongType(final SQLType sqlType, final int capacity) {
        super(array(Long.class, long.class), sqlType, capacity);
    }

    @Override
    protected Long doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getLong(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Long value) throws SQLException {
        stmt.setLong(parameterIndex, value);
    }
}
