package org.dru.dusaf.json.conf;

public interface JsonConf {
    <T> T get(final Class<T> type, final String name);
}
