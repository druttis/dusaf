package org.dru.dusaf.plugin;

import java.util.Arrays;
import java.util.List;

public enum ClassStrategy {
    APPLICATION_PLUGIN_DEPENDENCY(ClassSource.APPLICATION, ClassSource.PLUGIN, ClassSource.DEPENDENCY),
    APPLICATION_DEPENDENCY_PLUGIN(ClassSource.APPLICATION, ClassSource.DEPENDENCY, ClassSource.PLUGIN),
    PLUGIN_APPLICATION_DEPENDENCY(ClassSource.PLUGIN, ClassSource.APPLICATION, ClassSource.DEPENDENCY),
    DEPENDENCY_APPLICATION_PLUGIN(ClassSource.DEPENDENCY, ClassSource.APPLICATION, ClassSource.PLUGIN),
    DEPENDENCY_PLUGIN_APPLICATION(ClassSource.DEPENDENCY, ClassSource.PLUGIN, ClassSource.APPLICATION),
    PLUGIN_DEPENDENCY_APPLICATION(ClassSource.PLUGIN, ClassSource.DEPENDENCY, ClassSource.APPLICATION);

    private final List<ClassSource> sources;

    ClassStrategy(final ClassSource... sources) {
        this.sources = Arrays.asList(sources);
    }

    public List<ClassSource> getSources() {
        return sources;
    }

    public Class<?> loadClass(final ClassStrategyContext context, final String name) throws ClassNotFoundException {
        for (final ClassSource source : sources) {
            try {
                return source.loadClass(context, name);
            } catch (final ClassNotFoundException exc) {
                // ignore.
            }
        }
        throw new ClassNotFoundException(name);
    }
}
