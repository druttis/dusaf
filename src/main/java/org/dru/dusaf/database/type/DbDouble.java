package org.dru.dusaf.database.type;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbDouble extends AbstractDbType<Double> {
    public static final DbDouble INSTANCE = new DbDouble();

    private DbDouble() {
        super(JDBCType.DOUBLE);
    }

    @Override
    protected Double doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getDouble(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Double value)
            throws SQLException {
        stmt.setDouble(parameterIndex, value);
    }
}
