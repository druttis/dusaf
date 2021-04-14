package org.dru.dusaf.database.type;

import org.dru.dusaf.io.OutputInputStream;
import org.dru.dusaf.serialization.TypeSerializer;
import org.dru.dusaf.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Collection;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class DbBlobType<T> extends AbstractDbType<T> {
    public static final boolean USE_HEADER = false;

    private static final int UNCOMPRESSED = 0;
    private static final int COMPRESSED = 1;

    private final TypeSerializer<T> serializer;

    public DbBlobType(final Class<T> type, final SQLType sqlType, final int capacity,
                      final TypeSerializer<T> serializer) {
        super(type, sqlType, capacity);
        Objects.requireNonNull(serializer, "serializer");
        this.serializer = serializer;
    }

    @Override
    public String getDDL(final Connection conn) throws SQLException {
        return String.format("%s(%d)", super.getDDL(conn), getCapacity());
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final Collection<T> values)
            throws SQLException {
        throw new SQLException("can not set blob parameters");
    }

    @Override
    protected T doGet(final ResultSet rset, final int columnIndex) throws SQLException {
        try (final InputStream in = rset.getBinaryStream(columnIndex)) {
            if (in == null) {
                return null;
            }
            if (USE_HEADER) {
                final int compressed = in.read();
                if (compressed == -1) {
                    throw new SQLException("end of file reached while reading compressed state");
                }
                if (compressed == COMPRESSED) {
                    try (final GZIPInputStream gzip = new GZIPInputStream(in)) {
                        return decode(gzip);
                    } catch (final IOException exc) {
                        throw new SQLException("error while creating gzip input stream", exc);
                    }
                }
                return decode(in);
            } else {
                final byte[] bytes = IOUtils.readBytes(in);
                try (final GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
                    return decode(gzip);
                } catch (final IOException exc) {
                    return decode(new ByteArrayInputStream(bytes));
                }
            }
        } catch (final IOException exc) {
            throw new SQLException("error getting blob input stream", exc);
        }
    }

    @Override
    protected void doSet(final PreparedStatement stmt, final int parameterIndex, final T value) throws SQLException {
        try {
            int length = serializer.numBytes(value);
            if (length < -1) {
                throw new SQLException("negative value length:" + length);
            } else if (length == -1) {
                final OutputInputStream out = new OutputInputStream();
                length = out.size();
            }
            final OutputInputStream out = new OutputInputStream();
            if (length <= getCapacity()) {
                if (USE_HEADER) {
                    out.write(UNCOMPRESSED);
                }
                encode(out, value);
            } else {
                if (USE_HEADER) {
                    out.write(COMPRESSED);
                }
                try (final GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                    encode(gzip, value);
                } catch (final IOException exc) {
                    throw new SQLException("error while creating gzip output stream", exc);
                }
            }
            stmt.setBinaryStream(parameterIndex, out.getInputStream());
        } catch (final RuntimeException exc) {
            throw new SQLException("error while getting value length", exc);
        }
    }

    private T decode(final InputStream in) throws SQLException {
        try {
            return serializer.decode(in);
        } catch (final IOException exc) {
            throw new SQLException("error while decoding", exc);
        }
    }

    private void encode(final OutputStream out, final T value) throws SQLException {
        try {
            serializer.encode(out, value);
        } catch (final IOException exc) {
            throw new SQLException("error while encoding", exc);
        }
    }
}
