package org.dru.dusaf.json.conf;

import org.dru.dusaf.conf.ConfImpl;
import org.dru.dusaf.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class JsonConfImpl implements JsonConf {
    private static final Logger logger = LoggerFactory.getLogger(JsonConfImpl.class);

    private final JsonSerializer jsonSerializer;
    private final Map<String, Object> configs;

    public JsonConfImpl(final JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
        configs = new ConcurrentHashMap<>();
    }

    @Override
    public <T> T get(final Class<T> type, final String name) {
        return get(type, name, true);
    }

    @Override
    public <T> T get(final Class<T> type, final String name, final Supplier<T> supplier) {
        return Optional.ofNullable(get(type, name, false)).orElseGet(supplier);
    }

    private <T> T get(final Class<T> type, final String name, final boolean report) {
        return type.cast(getConfig(type, name, report));
    }

    private Object getConfig(final Class<?> type, final String name, final boolean report) {
        return configs.computeIfAbsent(name, $ -> {
            final String file = "/" + name + ".json";
            logger.info("loading conf: " + file);
            try (final InputStream in = ConfImpl.class.getResourceAsStream(file)) {
                return jsonSerializer.read(in).decode(type);
            } catch (final Exception exc) {
                if (report) {
                    logger.error("failed to load conf: " + file, exc);
                }
                return null;
            }
        });
    }
}
