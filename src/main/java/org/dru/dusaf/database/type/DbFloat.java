package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbFloat extends AbstractDbType<Float> {
    public static final DbFloat BOXED = new DbFloat(Float.class);
    public static final DbFloat PRIMITIVE = new DbFloat(float.class);

    private DbFloat(final Class<Float> type) {
        super(type, MysqlType.FLOAT, false, 0, 0);
    }

    @Override
    protected Float doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getFloat(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Float value)
            throws SQLException {
        stmt.setFloat(parameterIndex, value);
    }
}
