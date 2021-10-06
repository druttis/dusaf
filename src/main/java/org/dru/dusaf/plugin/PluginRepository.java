package org.dru.dusaf.plugin;

import java.nio.file.Path;
import java.util.List;

public interface PluginRepository {
    List<Path> getPluginPaths();

    boolean addPluginRootRoot(Path path);

    boolean removePluginRootPath(Path path);
}
