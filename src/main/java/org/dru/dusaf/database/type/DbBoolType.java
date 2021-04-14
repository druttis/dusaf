package org.dru.dusaf.database.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Collection;

public final class DbBoolType extends AbstractDbType<Boolean> {
    public DbBoolType(final SQLType sqlType, final int capacity) {
        super(array(Boolean.class, boolean.class), sqlType, capacity);
    }

    @Override
    protected Boolean doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getBoolean(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Boolean value)
            throws SQLException {
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Collection<Boolean> values)
            throws SQLException {
    }
}
