package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum DoubleArraySerializer implements TypeSerializer<double[]> {
    INSTANCE;

    @Override
    public double[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getDoubles(IOUtils.readFully(in, ArrayUtils.numDoublesInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final double[] value) {
        return ArrayUtils.numDoublesInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final double[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
