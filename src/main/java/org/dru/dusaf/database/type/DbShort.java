package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbShort extends AbstractDbType<Short> {
    public static final DbShort INSTANCE = new DbShort();

    private DbShort() {
        super(JDBCType.SMALLINT);
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
