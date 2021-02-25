package org.dru.dusaf.json.conf;

import org.dru.dusaf.conf.ConfImpl;
import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.json.JsonSerializerSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class JsonConfImpl implements JsonConf {
    private static final Logger logger = LoggerFactory.getLogger(JsonConfImpl.class);

    private final JsonSerializer jsonSerializer;
    private final Map<String, Object> configs;

    public JsonConfImpl(final JsonSerializerSupplier jsonSerializerSupplier) {
        jsonSerializer = jsonSerializerSupplier.get();
        configs = new ConcurrentHashMap<>();
    }

    @Override
    public <T> T get(final Class<T> type, final String name) {
        return type.cast(getConfig(type, name));
    }

    private Object getConfig(final Class<?> type, final String name) {
        return configs.computeIfAbsent(name, $ -> {
            final String file = "/" + name + ".json";
            logger.info("loading conf: " + file);
            try (final InputStream in = ConfImpl.class.getResourceAsStream(file)) {
                return jsonSerializer.readObject(in, type);
            } catch (final Exception exc) {
                logger.error("failed to load conf: " + file, exc);
                return null;
            }
        });
    }
}
