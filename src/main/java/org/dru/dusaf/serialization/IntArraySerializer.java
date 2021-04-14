package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum IntArraySerializer implements TypeSerializer<int[]> {
    INSTANCE;

    @Override
    public int[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getInts(IOUtils.readFully(in, ArrayUtils.numIntsInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final int[] value) {
        return ArrayUtils.numIntsInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final int[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
