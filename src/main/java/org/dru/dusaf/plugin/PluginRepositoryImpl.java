package org.dru.dusaf.plugin;

import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class PluginRepositoryImpl implements PluginRepository {
    private static final Path ZIP = Paths.get(".zip");
    private static final Path JAR = Paths.get(".jar");

    private static boolean isDirectoryOrZipOrJar(final Path path) {
        return Files.isDirectory(path) || (Files.isRegularFile(path) && (path.endsWith(JAR) || path.endsWith(ZIP)));
    }

    private final List<Path> rootPaths;

    public PluginRepositoryImpl() {
        rootPaths = new CopyOnWriteArrayList<>();
    }

    @Override
    public List<Path> getPluginPaths() {
        return null;
    }

    @Override
    public boolean addPluginRootRoot(final Path path) {
        Objects.requireNonNull(path, "pluginPath");
        if (!isDirectoryOrZipOrJar(path)) {
            return false;
        }
        synchronized (rootPaths) {
            if (!rootPaths.contains(path)) {
                rootPaths.add(path);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean removePluginRootPath(final Path path) {
        synchronized (rootPaths) {
            return rootPaths.remove(path);
        }
    }
}
