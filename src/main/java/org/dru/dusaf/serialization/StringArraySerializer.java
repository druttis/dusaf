package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum StringArraySerializer implements TypeSerializer<String[]> {
    INSTANCE;

    @Override
    public String[] decode(final InputStream in) throws IOException {
        final int n = IOUtils.readVarInt(in);
        final String[] value = new String[n];
        for (int i = 0; i < n; i++) {
            value[i] = StringSerializer.INSTANCE.decode(in);
        }
        return value;
    }

    @Override
    public int numBytes(final String[] value) {
        int b = 0;
        for (String v : value) {
            final int l = StringSerializer.INSTANCE.numBytes(v);
            b += l;
            b += ArrayUtils.getVarIntLen(l);
        }
        return b;
    }

    @Override
    public void encode(final OutputStream out, final String[] value) throws IOException {
        IOUtils.writeVarInt(out, value.length);
        for (final String v : value) {
            StringSerializer.INSTANCE.encode(out, v);
        }
    }
}
