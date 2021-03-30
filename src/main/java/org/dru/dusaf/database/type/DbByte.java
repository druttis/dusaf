package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbByte extends AbstractDbType<Byte> {
    public static DbByte INSTANCE = new DbByte();

    private DbByte() {
        super(JDBCType.TINYINT);
    }

    @Override
    protected Byte doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getByte(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Byte value) throws SQLException {
        stmt.setByte(parameterIndex, value);
    }
}
