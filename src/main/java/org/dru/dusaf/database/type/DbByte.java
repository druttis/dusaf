package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbByte extends AbstractDbType<Byte> {
    public static DbByte BOXED = new DbByte(Byte.class);
    public static DbByte PRIMITIVE = new DbByte(byte.class);

    private DbByte(final Class<Byte> type) {
        super(type, MysqlType.TINYINT, false, 0, 0);
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
