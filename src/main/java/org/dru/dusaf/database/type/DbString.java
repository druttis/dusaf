package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbString extends AbstractDbType<String> {
    public static final DbString INSTANCE = new DbString();

    private DbString() {
        super(JDBCType.VARCHAR, true);
    }

    @Override
    protected String doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getString(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final String value)
            throws SQLException {
        stmt.setString(parameterIndex, value);
    }
}
