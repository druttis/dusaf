package org.dru.dusaf.json.jackson;

import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.json.JsonSerializerSupplier;

public final class JacksonJsonSerializerSupplier implements JsonSerializerSupplier {
    private final JacksonJsonSerializer instance;

    public JacksonJsonSerializerSupplier() {
        instance = new JacksonJsonSerializer();
    }

    @Override
    public JsonSerializer get() {
        return instance;
    }
}
