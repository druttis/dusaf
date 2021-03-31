package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbCharacter extends AbstractDbType<Character> {
    public static final DbCharacter BOXED = new DbCharacter(Character.class);
    public static final DbCharacter PRIMITIVE = new DbCharacter(char.class);

    private DbCharacter(final Class<Character> type) {
        super(type, MysqlType.CHAR, false, 0, 0);
    }

    @Override
    protected Character doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        final String s = rset.getString(columnIndex);
        return (s != null ? s.charAt(0) : null);
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Character value)
            throws SQLException {
        stmt.setString(parameterIndex, value.toString());
    }
}
