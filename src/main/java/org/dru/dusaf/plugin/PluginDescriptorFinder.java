package org.dru.dusaf.plugin;

import java.nio.file.Path;

public interface PluginDescriptorFinder {
    boolean supports(Path path);

    PluginDescriptor find(Path path);
}
