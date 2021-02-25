package org.dru.dusaf.conf;

public interface Conf {
    String get(String name);

    String get(String name, String defaultValue);
}
