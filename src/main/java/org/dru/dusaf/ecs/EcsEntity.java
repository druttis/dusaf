package org.dru.dusaf.ecs;

import org.dru.dusaf.util.Bits;

import java.util.Arrays;

public final class EcsEntity {
    private final EcsEngine engine;
    private final Bits aspectBits;
    private Object[] components;
    private int[] localIds;

    EcsEntity(final EcsEngine engine) {
        this.engine = engine;
        aspectBits = new Bits();
        components = new Object[4];
        localIds = new int[1];
    }

    public void killEntity() {
        engine.removeEntity(this);
    }

    Bits getAspectBits() {
        return aspectBits;
    }

    Object getComponent(final int componentId) {
        return (componentId < components.length ? components[componentId] : null);
    }

    void setComponent(final int componentId, final Object component) {
        final Object old = getComponent(componentId);
        if (component != old) {
            if (component != null && old == null) {
                // add
                if (componentId >= components.length) {
                    components = Arrays.copyOf(components, ((componentId + 1) * 3) >> 1);
                }
                components[componentId] = component;
                engine.onEntityAspectChanged(this);
            } else if (component == null) {
                // remove
                components[componentId] = null;
                aspectBits.clear(componentId);
                engine.onEntityAspectChanged(this);
            } else {
                // update
                components[componentId] = component;
            }
        }
    }

    void clearComponents() {
        Arrays.fill(components, null);
    }

    int getLocalId(final int containerId) {
        return (containerId < localIds.length ? localIds[containerId] - 1 : -1);
    }

    void setLocalId(final int containerId, final int localId) {
        if (localId != -1) {
            if (containerId >= localIds.length) {
                localIds = Arrays.copyOf(localIds, ((containerId + 1) * 3) >> 1);
            }
            localIds[containerId] = localId + 1;
        } else {
            localIds[containerId] = 0;
        }
    }
}
