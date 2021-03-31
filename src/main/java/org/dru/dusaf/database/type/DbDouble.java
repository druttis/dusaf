package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbDouble extends AbstractDbType<Double> {
    public static final DbDouble BOXED = new DbDouble(Double.class);
    public static final DbDouble PRIMITIVE = new DbDouble(double.class);

    private DbDouble(final Class<Double> type) {
        super(type, MysqlType.DOUBLE, false, 0, 0);
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
