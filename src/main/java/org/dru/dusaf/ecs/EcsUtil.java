package org.dru.dusaf.ecs;

import org.dru.dusaf.util.Bag;

public final class EcsUtil {
    private EcsUtil() {
    }

    public static void add(final Bag<EcsEntity> entities, final int containerId, final EcsEntity entity) {
        if (entity.getLocalId(containerId) != -1) {
            throw new IllegalArgumentException("entity already added to a container with specified id");
        }
        final int localId = entities.size();
        entity.setLocalId(containerId, localId);
        entities.add(entity);
    }

    public static void remove(final Bag<EcsEntity> entities, final int containerId, final EcsEntity entity) {
        final int localId = entity.getLocalId(containerId);
        entity.setLocalId(containerId, -1);
        entities.remove(localId);
        final EcsEntity moved = entities.get(localId);
        moved.setLocalId(containerId, localId);
    }
}
