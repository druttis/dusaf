package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum FloatArraySerializer implements TypeSerializer<float[]> {
    INSTANCE;

    @Override
    public float[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getFloats(IOUtils.readFully(in, ArrayUtils.numFloatsInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final float[] value) {
        return ArrayUtils.numFloatsInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final float[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
