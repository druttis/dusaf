package org.dru.dusaf.database.type;

import com.mysql.cj.MysqlType;
import org.dru.dusaf.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public final class DbByteArray extends AbstractDbType<byte[]> {
    public static final DbByteArray TINY = new DbByteArray(MysqlType.TINYBLOB, 1, 255);
    public static final DbByteArray SMALL = new DbByteArray(MysqlType.BLOB, 256, 65535);
    public static final DbByteArray MEDIUM = new DbByteArray(MysqlType.MEDIUMBLOB, 65536, 16777215);
    public static final DbByteArray LONG = new DbByteArray(MysqlType.LONGBLOB, 16777216, Integer.MAX_VALUE);

    private DbByteArray(final SQLType sqlType, final int minLength, final int maxLength) {
        super(byte[].class, sqlType, true, minLength, maxLength);
    }

    @Override
    protected byte[] doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        final InputStream in = rset.getBinaryStream(columnIndex);
        if (in == null) {
            return null;
        }
        try {
            return IOUtils.readBytes(in);
        } catch (final IOException exc) {
            throw new SQLException("failed to ready bytes", exc);
        }
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final byte[] value)
            throws SQLException {
        stmt.setBinaryStream(parameterIndex, new ByteArrayInputStream(value));
    }
}
