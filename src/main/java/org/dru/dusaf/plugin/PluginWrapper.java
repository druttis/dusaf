package org.dru.dusaf.plugin;

import java.nio.file.Path;

public class PluginWrapper {
    private PluginManager manager;
    private PluginDescriptor descriptor;
    private Path path;
    private ClassLoader classLoader;
    private PluginState state;
    private Exception error;
    private Plugin plugin;
}
