package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbBoolean extends AbstractDbType<Boolean> {
    public static final DbBoolean INSTANCE = new DbBoolean();

    private DbBoolean() {
        super(JDBCType.BIT);
    }

    @Override
    protected Boolean doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getBoolean(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Boolean value)
            throws SQLException {
        stmt.setBoolean(parameterIndex, value);
    }
}
