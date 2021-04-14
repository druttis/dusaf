package org.dru.dusaf.serialization;

import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.json.JsonSerializerSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class JsonDataSerializer<T> implements TypeSerializer<T> {

    private final Class<T> type;
    private final JsonSerializer jsonSerializer;

    public JsonDataSerializer(final Class<T> type, final JsonSerializerSupplier jsonSerializerSupplier) {
        this.type = type;
        jsonSerializer = jsonSerializerSupplier.get();
    }

    @Override
    public T decode(final InputStream in) throws IOException {
        return jsonSerializer.readObject(in, type);
    }

    @Override
    public int numBytes(final T value) {
        return -1;
    }

    @Override
    public void encode(final OutputStream out, final T value) throws IOException {
        jsonSerializer.writeObject(out, value);
    }
}
