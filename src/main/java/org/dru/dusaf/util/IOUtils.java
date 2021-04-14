package org.dru.dusaf.util;

import java.io.*;

public final class IOUtils {
    public static long copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[8192];
        long totalBytes = 0;
        int readBytes;
        while ((readBytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, readBytes);
            totalBytes += readBytes;
        }
        return totalBytes;
    }

    public static byte[] readBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    public static void readFully(final InputStream in, final byte[] buf, final int off, final int len)
            throws IOException {
        final int end = off + len;
        int pos = off;
        while (pos < len) {
            final int read = in.read(buf, pos, end - pos);
            if (read == -1) {
                throw new EOFException();
            }
            pos += read;
        }
    }

    public static void readFully(final InputStream in, final byte[] buf) throws IOException {
        readFully(in, buf, 0, buf.length);
    }

    public static byte[] readFully(final InputStream in, final int len) throws IOException {
        final byte[] buf = new byte[len];
        readFully(in, buf);
        return buf;
    }

    public static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException exc) {
                // do nothing!
            }
        }
    }

    public static int readVarInt(final InputStream in) throws IOException {
        int val = 0;
        int b;
        while (((b = in.read()) & 0x80) != 0) {
            if (b == -1) {
                throw new EOFException();
            }
            val = (val << 7) | (b & 0x7f);
        }
        return val;
    }

    public static void writeVarInt(final OutputStream out, final int val) throws IOException {
        if (val > 0x0FFFFFFF || val < 0) {
            out.write((byte) (0x80 | ((val >>> 28))));
        }
        if (val > 0x1FFFFF || val < 0) {
            out.write((byte) (0x80 | ((val >>> 21) & 0x7F)));
        }
        if (val > 0x3FFF || val < 0) {
            out.write((byte) (0x80 | ((val >>> 14) & 0x7F)));
        }
        if (val > 0x7F || val < 0) {
            out.write((byte) (0x80 | ((val >>> 7) & 0x7F)));
        }
        out.write((byte) (val & 0x7F));
    }

    private IOUtils() {
    }
}
