package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum ShortArraySerializer implements TypeSerializer<short[]> {
    INSTANCE;

    @Override
    public short[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getShorts(IOUtils.readFully(in, ArrayUtils.numShortsInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final short[] value) {
        return ArrayUtils.numShortsInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final short[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
