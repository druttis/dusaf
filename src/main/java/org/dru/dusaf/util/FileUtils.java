package org.dru.dusaf.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class FileUtils {
    public static void delete(final Path path) throws IOException {
        for (final Path current : Files.walk(path).sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
            Files.delete(current);
        }
    }

    public static Predicate<Path> endsWith(final String str) {
        return path -> path.toString().endsWith(str);
    }

    public static Predicate<Path> endsWithIgnoreCase(final String str) {
        return path -> path.toString().toLowerCase().endsWith(str.toLowerCase());
    }

    public static Predicate<Path> isJar() {
        return endsWith(".jar");
    }

    public static boolean isJar(final Path path) {
        return isJar().test(path);
    }

    public static Predicate<Path> isZip() {
        return endsWith(".zip");
    }

    public static boolean isZip(final Path path) {
        return isZip().test(path);
    }

    public static Predicate<Path> exists() {
        return Files::exists;
    }

    public static boolean exists(final Path path) {
        return exists().test(path);
    }

    public static Predicate<Path> isDirectory() {
        return Files::isDirectory;
    }

    public static boolean isDirectory(final Path path) {
        return isDirectory().test(path);
    }

    public static Path findFile(final Path path, final String filename) {
        final File[] files = path.toFile().listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isFile()) {
                    if (file.getName().equals(filename)) {
                        return file.toPath();
                    }
                } else if (file.isDirectory()) {
                    final Path found = findFile(file.toPath(), filename);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    private FileUtils() {
    }
}
