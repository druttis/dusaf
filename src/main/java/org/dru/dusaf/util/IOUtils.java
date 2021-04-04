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

    public static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException exc) {
                // do nothing!
            }
        }
    }

    private IOUtils() {
    }
}
