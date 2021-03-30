package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbInteger extends AbstractDbType<Integer> {
    public static final DbInteger INSTANCE = new DbInteger();

    private DbInteger() {
        super(JDBCType.INTEGER);
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
