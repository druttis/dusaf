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
    protected Float getResultImpl(final ResultSet rset, final int index) throws SQLException {
        return rset.getFloat(index);
    }

    @Override
    protected void setParameterImpl(final PreparedStatement stmt, final int index, final Float value)
            throws SQLException {
        stmt.setFloat(index, value);
    }
}
