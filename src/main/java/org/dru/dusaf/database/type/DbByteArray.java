package org.dru.dusaf.database.type;

import org.dru.dusaf.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbByteArray extends AbstractDbType<byte[]> {
    public static final DbByteArray INSTANCE = new DbByteArray();

    private DbByteArray() {
        super(JDBCType.BLOB, true);
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
