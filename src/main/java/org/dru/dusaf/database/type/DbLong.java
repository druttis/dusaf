package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbLong extends AbstractDbType<Long> {
    public static final DbLong INSTANCE = new DbLong();

    private DbLong() {
        super(JDBCType.BIGINT);
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
