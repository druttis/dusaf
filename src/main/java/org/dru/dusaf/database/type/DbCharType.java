package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbCharType extends AbstractDbType<Character> {
    public DbCharType(final SQLType sqlType, final int capacity) {
        super(array(Character.class, char.class), sqlType, capacity);
    }

    @Override
    protected Character doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        final String s = rset.getString(columnIndex);
        return (s != null ? s.charAt(0) : null);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Character value)
            throws SQLException {
        stmt.setString(parameterIndex, value.toString());
    }
}
