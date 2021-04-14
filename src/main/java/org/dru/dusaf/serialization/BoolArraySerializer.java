package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum BoolArraySerializer implements TypeSerializer<boolean[]> {
    INSTANCE;

    @Override
    public boolean[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        return ArrayUtils.getBooleans(IOUtils.readFully(in, ArrayUtils.numBooleansInBytes(n)), 0, n);
    }

    @Override
    public int numBytes(final boolean[] value) {
        return ArrayUtils.numBooleansInBytes(value.length);
    }

    @Override
    public void encode(final OutputStream out, final boolean[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        out.write(ArrayUtils.getBytes(value, 0, value.length));
    }
}
