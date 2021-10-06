package org.dru.dusaf.plugin;

public interface ClassStrategyContext {
    Class<?> loadApplicationClass(String name) throws ClassNotFoundException;

    Class<?> loadPluginClass(String name) throws ClassNotFoundException;

    Class<?> loadDependencyClass(String name) throws ClassNotFoundException;
}
