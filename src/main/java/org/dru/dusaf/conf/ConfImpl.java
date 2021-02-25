package org.dru.dusaf.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfImpl implements Conf {
    private static final Logger logger = LoggerFactory.getLogger(ConfImpl.class);

    private final Map<String, Properties> propertiesByName;

    public ConfImpl() {
        propertiesByName = new ConcurrentHashMap<>();
    }

    @Override
    public String get(final String name) {
        final String value = getProperties(name).getProperty(name);
        if (value == null) {
            throw new NoSuchElementException(name);
        }
        return value;
    }

    @Override
    public String get(final String name, final String defaultValue) {
        return getProperties(name).getProperty(name, defaultValue);
    }

    private Properties getProperties(final String name) {
        final int index = name.indexOf(".");
        if (index == -1) {
            throw new IllegalArgumentException("expecting <file>.<name...>");
        }
        final String file = "/" + name.substring(0, index) + ".properties";
        return propertiesByName.computeIfAbsent(file, $ -> {
            final Properties properties = new Properties();
            try (final InputStream in = ConfImpl.class.getResourceAsStream(file)) {
                properties.load(in);
                logger.info("loaded config: " + file);
            } catch (final IOException exc) {
                logger.warn("failed to load config: " + file, exc);
            }
            return properties;
        });
    }
}
