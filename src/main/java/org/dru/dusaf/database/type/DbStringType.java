package org.dru.dusaf.database.type;

import java.sql.*;

public final class DbStringType extends AbstractDbType<String> {
    public DbStringType(final SQLType sqlType, final int capacity) {
        super(String.class, sqlType, capacity);
    }

    @Override
    protected String doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getString(columnIndex);
    }

    @Override
    public String getDDL(final Connection conn) throws SQLException {
        return String.format("%s(%d)", super.getDDL(conn), getCapacity());
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final String value)
            throws SQLException {
        stmt.setString(parameterIndex, value);
    }
}
