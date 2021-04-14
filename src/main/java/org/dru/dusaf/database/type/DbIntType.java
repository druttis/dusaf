package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbIntType extends AbstractDbType<Integer> {
    public DbIntType(final SQLType sqlType, final int capacity) {
        super(array(Integer.class, int.class), sqlType, capacity);
    }

    @Override
    protected Integer doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getInt(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Integer value)
            throws SQLException {
        stmt.setInt(parameterIndex, value);
    }
}
