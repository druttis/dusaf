package org.dru.dusaf.ecs;

import java.util.Objects;

public final class EcsMapping<T> {
    private final int componentId;
    private final Class<T> componentType;

    public EcsMapping(final int componentId, final Class<T> componentType) {
        if (componentId < 0) {
            throw new IllegalArgumentException("negative componentId: " + componentId);
        }
        Objects.requireNonNull(componentType, "componentType");
        this.componentId = componentId;
        this.componentType = componentType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EcsMapping<?> that = (EcsMapping<?>) o;
        return componentId == that.componentId && componentType.equals(that.componentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, componentType);
    }

    @Override
    public String toString() {
        return "EcsMapping{" +
                "componentId=" + componentId +
                ", componentType=" + componentType +
                '}';
    }

    public T getComponent(final EcsEntity entity) {
        return componentType.cast(entity.getComponent(componentId));
    }

    public void setComponent(final EcsEntity entity, final T component) {
        entity.setComponent(componentId, component);
    }

    int getComponentId() {
        return componentId;
    }

    Class<T> getComponentType() {
        return componentType;
    }
}
