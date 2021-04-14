package org.dru.dusaf.serialization;

import org.dru.dusaf.util.ArrayUtils;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public enum StringSerializer implements TypeSerializer<String> {
    INSTANCE;

    @Override
    public String decode(final InputStream in) throws IOException {
        final int len = IOUtils.readVarInt(in);
        return new String(IOUtils.readFully(in, len), StandardCharsets.UTF_8);
    }

    @Override
    public int numBytes(final String value) {
        return ArrayUtils.utf8Length(value);
    }

    @Override
    public void encode(final OutputStream out, final String value) throws IOException {
        final byte[] buf = value.getBytes(StandardCharsets.UTF_8);
        IOUtils.writeVarInt(out, buf.length);
        out.write(buf);
    }
}
