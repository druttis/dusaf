package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;

public final class DbString extends AbstractDbType<String> {
    public static final DbString TINY = new DbString(MysqlType.TINYTEXT, 1, 255);
    public static final DbString SMALL = new DbString(MysqlType.TEXT, 256, 65535);
    public static final DbString MEDIUM = new DbString(MysqlType.MEDIUMTEXT, 65536, 16777215);
    public static final DbString LONG = new DbString(MysqlType.LONGTEXT, 16777216, Integer.MAX_VALUE);

    private DbString(final SQLType sqlType, final int minLength, final int maxLength) {
        super(String.class, sqlType, true, minLength, maxLength);
    }

    @Override
    protected String doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        return rset.getString(columnIndex);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final String value)
            throws SQLException {
        stmt.setString(parameterIndex, value);
    }
}
