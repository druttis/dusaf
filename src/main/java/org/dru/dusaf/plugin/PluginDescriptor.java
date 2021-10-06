package org.dru.dusaf.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class PluginDescriptor {
    private String id;
    private String description;
    private String className;
    private String version;
    private List<PluginDependency> dependencies;

    public PluginDescriptor(final String id, final String description, final String className, final String version,
                            final PluginDependency[] dependencies) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(dependencies, "dependencies");
        for (final PluginDependency dependency : dependencies) {
            Objects.requireNonNull(dependency, "dependency");
        }
        this.id = id;
        this.description = description;
        this.className = className;
        this.version = version;
        this.dependencies = Arrays.asList(dependencies);
    }

    public PluginDescriptor() {
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getClassName() {
        return className;
    }

    public String getVersion() {
        return version;
    }

    public List<PluginDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PluginDescriptor that = (PluginDescriptor) o;
        return id.equals(that.id) && description.equals(that.description) && className.equals(that.className)
                && version.equals(that.version) && dependencies.equals(that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, className, version, dependencies);
    }

    @Override
    public String toString() {
        return "PluginDescriptor{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", className='" + className + '\'' +
                ", version='" + version + '\'' +
                ", dependencies=" + dependencies +
                '}';
    }
}
