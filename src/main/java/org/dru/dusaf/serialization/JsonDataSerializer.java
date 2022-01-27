package org.dru.dusaf.serialization;

import org.dru.dusaf.json.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class JsonDataSerializer<T> implements TypeSerializer<T> {

    private final Class<T> type;
    private final JsonSerializer jsonSerializer;

    public JsonDataSerializer(final Class<T> type, final JsonSerializer jsonSerializer) {
        this.type = type;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public T decode(final InputStream in) throws IOException {
        return jsonSerializer.read(in).decode(type);
    }

    @Override
    public int numBytes(final T value) {
        return -1;
    }

    @Override
    public void encode(final OutputStream out, final T value) throws IOException {
        jsonSerializer.encode(value).write(out);
    }
}
