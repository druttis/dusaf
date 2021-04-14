package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbDoubleType extends AbstractDbType<Double> {
    public DbDoubleType(final SQLType sqlType, final int capacity) {
        super(array(Double.class, double.class), sqlType, capacity);
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
