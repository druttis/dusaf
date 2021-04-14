package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum LongArraySerializer implements TypeSerializer<long[]> {
    INSTANCE;

    @Override
    public long[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getLongs(IOUtils.readFully(in, ArrayUtils.numLongsInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final long[] value) {
        return ArrayUtils.numLongsInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final long[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
