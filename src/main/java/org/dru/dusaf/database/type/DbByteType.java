package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbByteType extends AbstractDbType<Byte> {
    public DbByteType(final SQLType sqlType, final int capacity) {
        super(array(Byte.class, byte.class), sqlType, capacity);
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
