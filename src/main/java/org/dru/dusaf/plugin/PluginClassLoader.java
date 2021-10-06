package org.dru.dusaf.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

public class PluginClassLoader extends URLClassLoader implements ClassStrategyContext {
    private static final Logger logger = LoggerFactory.getLogger(PluginClassLoader.class);
    private static final URL[] DEFAULT_URLS = {};

    private final PluginManager manager;
    private final PluginDescriptor descriptor;
    private final ClassStrategy strategy;

    public PluginClassLoader(final PluginManager manager, final PluginDescriptor descriptor,
                             final ClassStrategy strategy) {
        super(DEFAULT_URLS);
        Objects.requireNonNull(manager, "manager");
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(strategy, "strategy");
        this.manager = manager;
        this.descriptor = descriptor;
        this.strategy = strategy;
    }

    @Override
    public void addURL(final URL url) {
        Objects.requireNonNull(url, "url");
        logger.debug("adding url: {}", url);
        super.addURL(url);
        logger.info("url added: {}", url);
    }

    public void addFile(final File file) {
        Objects.requireNonNull(file, "file");
        try {
            logger.debug("adding file: {}", file);
            super.addURL(file.getCanonicalFile().toURI().toURL());
            logger.info("file added: {}", file);
        } catch (final IOException exc) {
            logger.error("failed to add file: {}", file, exc);
        }
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name.startsWith("java.")) {
                return findSystemClass(name);
            }
            if (name.startsWith("org.dru.dusaf")) {
                return getParent().loadClass(name);
            }
            final Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                return loaded;
            }
            return strategy.loadClass(this, name);
        }
    }

    @Override
    public Class<?> loadApplicationClass(final String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    public Class<?> loadPluginClass(final String name) throws ClassNotFoundException {
        return findClass(name);
    }

    @Override
    public Class<?> loadDependencyClass(final String name) throws ClassNotFoundException {
        final List<PluginDependency> dependencies = descriptor.getDependencies();
        for (final PluginDependency dependency : dependencies) {
            final ClassLoader classLoader = manager.getClassLoader(dependency.getId());
            if (classLoader == null) {
                if (dependency.isOptional()) {
                    continue;
                } else {
                    continue;
                }
            }
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public URL getResource(final String name) {
        return super.getResource(name);
    }
}
