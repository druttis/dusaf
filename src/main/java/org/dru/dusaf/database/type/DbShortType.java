package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbShortType extends AbstractDbType<Short> {
    public DbShortType(final SQLType sqlType, final int capacity) {
        super(array(Short.class, short.class), sqlType, capacity);
    }

    @Override
    protected Short doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getShort(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Short value)
            throws SQLException {
        stmt.setShort(parameterIndex, value);
    }
}
