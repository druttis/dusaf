package org.dru.dusaf.json.conf;

import java.util.function.Supplier;

public interface JsonConf {
    <T> T get(final Class<T> type, final String name);

    <T> T get(final Class<T> type, final String name, Supplier<T> supplier);
}
