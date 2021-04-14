package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbFloatType extends AbstractDbType<Float> {
    public DbFloatType(final SQLType sqlType, final int capacity) {
        super(array(Float.class, float.class), sqlType, capacity);
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
