package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.*;

public final class DbBoolean extends AbstractDbType<Boolean> {
    public static final DbBoolean BOXED = new DbBoolean(Boolean.class);
    public static final DbBoolean PRIMITIVE = new DbBoolean(boolean.class);

    private DbBoolean(final Class<Boolean> type) {
        super(type, MysqlType.BIT, false, 0, 0);
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
