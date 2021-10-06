package org.dru.dusaf.plugin;

import java.util.List;

public interface PluginManager {
    List<PluginWrapper> getPlugins();

    List<PluginWrapper> getPlugins(PluginState state);

    List<PluginWrapper> getResolvedPlugins();

    List<PluginWrapper> getUnresolvedPlugins();

    List<PluginWrapper> getStartedPlugins();

    PluginWrapper getPlugin(String pluginId);

    void loadPlugins();

    void loadPlugin(String pluginId);

    void startPlugins();

    void startPlugin(String pluginId);

    void stopPlugins();

    void stopPlugin(String pluginId);

    void unloadPlugins();

    void unloadPlugin(String pluginId);

    ClassLoader getClassLoader(String pluginId);
}
