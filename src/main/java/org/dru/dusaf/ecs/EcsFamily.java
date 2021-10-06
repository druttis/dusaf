package org.dru.dusaf.ecs;

import org.dru.dusaf.util.Bag;

import java.util.Iterator;

final class EcsFamily implements Iterable<EcsEntity> {
    private final EcsAspect aspect;
    private final int containerId;
    private final Bag<EcsEntity> entities;

    EcsFamily(final EcsAspect aspect, final int containerId) {
        this.aspect = aspect;
        this.containerId = containerId;
        entities = new Bag<>();
    }

    @Override
    public Iterator<EcsEntity> iterator() {
        return entities.iterator();
    }

    void onEntityAdded(final EcsEntity entity) {
        if (aspect.test(entity.getAspectBits())) {
            addEntity(entity);
        }
    }

    void onEntityRemoved(final EcsEntity entity) {
        if (aspect.test(entity.getAspectBits())) {
            removeEntity(entity);
        }
    }

    void onEntityAspectChanged(final EcsEntity entity) {
        final boolean matching = aspect.test(entity.getAspectBits());
        final boolean contained = (entity.getLocalId(containerId) != -1);
        if (matching && !contained) {
            addEntity(entity);
        } else if (!matching && contained) {
            removeEntity(entity);
        }
    }

    private void addEntity(final EcsEntity entity) {
        EcsUtil.add(entities, containerId, entity);
    }

    private void removeEntity(final EcsEntity entity) {
        EcsUtil.remove(entities, containerId, entity);
    }
}
