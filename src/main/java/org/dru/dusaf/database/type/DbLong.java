package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbLong extends AbstractDbType<Long> {
    public static final DbLong BOXED = new DbLong(Long.class);
    public static final DbLong PRIMITIVE = new DbLong(long.class);

    private DbLong(final Class<Long> type) {
        super(type, MysqlType.BIGINT, false, 0, 0);
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
