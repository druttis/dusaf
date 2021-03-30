package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbFloat extends AbstractDbType<Float> {
    public static final DbFloat INSTANCE = new DbFloat();

    private DbFloat() {
        super(JDBCType.FLOAT);
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
