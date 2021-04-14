package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum CharArraySerializer implements TypeSerializer<char[]> {
    INSTANCE;

    @Override
    public char[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getChars(IOUtils.readFully(in, ArrayUtils.numCharsInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final char[] value) {
        return ArrayUtils.numCharsInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final char[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
