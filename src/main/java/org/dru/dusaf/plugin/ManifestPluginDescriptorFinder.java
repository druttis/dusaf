package org.dru.dusaf.plugin;

import org.dru.dusaf.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

public final class ManifestPluginDescriptorFinder implements PluginDescriptorFinder {
    public static final String ID = "Id";
    public static final String DESCRIPTION = "Description";
    public static final String CLASS_NAME = "Class-Name";
    public static final String VERSION = "Version";
    public static final String DEPENDENCIES = "Dependencies";

    public static Manifest readManifest(final Path path) {
        if (FileUtils.isJar(path)) {
            try (final JarFile jar = new JarFile(path.toFile())) {
                final Manifest manifest = jar.getManifest();
                if (manifest != null) {
                    return manifest;
                }
            } catch (final IOException exc) {
                throw new RuntimeException(exc);
            }
        }
        final Path manifestPath = getManifestPath(path);
        if (manifestPath == null) {
            throw new RuntimeException("can not find the manifest path");
        }
        if (Files.notExists(manifestPath)) {
            throw new RuntimeException("path does not exist: " + manifestPath);
        }
        try (final InputStream in = Files.newInputStream(manifestPath)) {
            return new Manifest(in);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static PluginDescriptor toDescriptor(final Manifest manifest) {
        final Attributes attributes = manifest.getMainAttributes();
        final String id = getAttributeValue(attributes, ID);
        final String description = getAttributeValue(attributes, DESCRIPTION);
        final String className = getAttributeValue(attributes, CLASS_NAME);
        final String version = getAttributeValue(attributes, VERSION);
        final String dependencies = getAttributeValue(attributes, DEPENDENCIES);
        checkNotEmpty(id, ID);
        checkNotEmpty(className, CLASS_NAME);
        checkNotEmpty(version, VERSION);
        final String[] parts = dependencies.split(",");
        final PluginDependency[] deps = Stream.of(parts).map(PluginDependency::parse).toArray(PluginDependency[]::new);
        return new PluginDescriptor(id, description, className, version, deps);
    }

    private static String getAttributeValue(final Attributes attributes, final String name) {
        return Optional.of(attributes.getValue(name)).orElse("").trim();
    }

    private static Path getManifestPath(Path path) {
        if (Files.isDirectory(path)) {
            return FileUtils.findFile(path, "MANIFEST.MF");
        }
        return null;
    }

    private static void checkNotEmpty(final String value, final String name) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("empty or missing " + name);
        }
    }

    public ManifestPluginDescriptorFinder() {
    }

    public boolean supports(final Path path) {
        return FileUtils.exists().and(FileUtils.isDirectory().or(FileUtils.isJar())).test(path);
    }

    @Override
    public PluginDescriptor find(final Path path) {
        return toDescriptor(readManifest(path));
    }
}
