package org.dru.dusaf.serialization;

import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum ByteArraySerializer implements TypeSerializer<byte[]> {
    INSTANCE;

    @Override
    public byte[] decode(final InputStream in) throws IOException {
        return IOUtils.readFully(in, IOUtils.readVarInt(in));
    }

    @Override
    public int numBytes(final byte[] value) {
        return value.length;
    }

    @Override
    public void encode(final OutputStream out, final byte[] array) throws IOException {
        IOUtils.writeVarInt(out, array.length);
        out.write(array);
    }
}
