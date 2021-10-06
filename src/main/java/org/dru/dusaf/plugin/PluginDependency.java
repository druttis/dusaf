package org.dru.dusaf.plugin;

import java.util.Objects;

public final class PluginDependency {
    public static PluginDependency parse(final String str) {
        final String[] parts = str.split("@");
        String id;
        String version;
        if (parts.length == 1) {
            id = parts[0].trim();
            version = "*";
        } else if (parts.length == 2) {
            id = parts[0].trim();
            version = parts[1].trim();
        } else {
            throw new IllegalArgumentException("expected <id>[?][@<version>]: '" + str + '\'');
        }
        final boolean optional = id.endsWith("?");
        if (optional) {
            id = id.substring(0, id.length() - 1);
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("invalid id syntax: '" + id + '\'');
        }
        if (version.isEmpty()) {
            throw new IllegalArgumentException("invalid version syntax: '" + version + '\'');
        }
        return new PluginDependency(id, version, optional);
    }

    private String id;
    private String version;
    private boolean optional;

    private PluginDependency(final String id, final String version, final boolean optional) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(version, "version");
        this.id = id;
        this.version = version;
        this.optional = optional;
    }

    public PluginDependency() {
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PluginDependency that = (PluginDependency) o;
        return optional == that.optional && id.equals(that.id) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, optional);
    }

    @Override
    public String toString() {
        return "PluginDependency{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", optional=" + optional +
                '}';
    }
}
