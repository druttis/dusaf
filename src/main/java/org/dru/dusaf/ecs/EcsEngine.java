package org.dru.dusaf.ecs;

import org.dru.dusaf.util.Bag;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class EcsEngine {
    private final EcsMappings mappings;
    private final AtomicBoolean updating;
    private final Map<EcsSystem, EcsWrapper> wrapperBySystem;
    private final List<EcsWrapper> wrappers;
    private final List<Runnable> pendingTasks;
    private final AtomicInteger familyIdCounter;
    private final Map<EcsAspect, EcsFamily> familyByAspect;
    private final Bag<EcsEntity> entities;

    EcsEngine(final EcsMappings mappings) {
        Objects.requireNonNull(mappings, "mappings");
        this.mappings = mappings;
        updating = new AtomicBoolean();
        wrapperBySystem = new HashMap<>();
        wrappers = new ArrayList<>();
        pendingTasks = new ArrayList<>();
        familyIdCounter = new AtomicInteger(1);
        familyByAspect = new HashMap<>();
        entities = new Bag<>();
    }

    public void addSystem(final EcsSystem system) {
        Objects.requireNonNull(system, "system");
        if (isUpdating()) {
            invokeLater(() -> addSystemInternal(system));
        } else {
            addSystemInternal(system);
        }
    }

    private void addSystemInternal(final EcsSystem system) {
        wrappers.add(wrapperBySystem.compute(system, ($, existing) -> {
            if (existing != null) {
                throw new IllegalArgumentException("system already added");
            }
            existing = new EcsWrapper(system);
            existing.map(mappings);
            existing.bind(this, mappings);
            existing.initialize(this);
            return existing;
        }));
    }

    public void removeSystem(final EcsSystem system) {
        if (system != null) {
            if (isUpdating()) {
                invokeLater(() -> removeSystemInternal(system));
            } else {
                removeSystemInternal(system);
            }
        }
    }

    private void removeSystemInternal(final EcsSystem system) {
        final EcsWrapper wrapper = wrapperBySystem.remove(system);
        if (wrapper != null) {
            wrappers.remove(wrapper);
            try {
                wrapper.destroy(this);
            } catch (final RuntimeException exc) {
                System.err.println("unhandled runtime-exception caught when removing system:");
                exc.printStackTrace();
            }
        }
    }

    public EcsEntity spawnEntity() {
        final EcsEntity entity = new EcsEntity(this);
        addEntity(entity);
        return entity;
    }

    boolean isUpdating() {
        return updating.get();
    }

    private void invokeLater(final Runnable task) {
        synchronized (pendingTasks) {
            pendingTasks.add(task);
        }
    }

    EcsFamily getFamily(final EcsAspect aspect) {
        return familyByAspect.computeIfAbsent(aspect, $ -> new EcsFamily(aspect, familyIdCounter.getAndIncrement()));
    }

    void addEntity(final EcsEntity entity) {
        if (isUpdating()) {
            invokeLater(() -> addEntityInternal(entity));
        } else {
            addEntityInternal(entity);
        }
    }

    private void addEntityInternal(final EcsEntity entity) {
        EcsUtil.add(entities, 0, entity);
        for (final EcsFamily family : familyByAspect.values()) {
            family.onEntityAdded(entity);
        }
    }

    void removeEntity(final EcsEntity entity) {
        if (isUpdating()) {
            invokeLater(() -> removeEntityInternal(entity));
        } else {
            removeEntityInternal(entity);
        }
    }

    private void removeEntityInternal(final EcsEntity entity) {
        EcsUtil.remove(entities, 0, entity);
        for (final EcsFamily family : familyByAspect.values()) {
            family.onEntityRemoved(entity);
        }
    }

    void onEntityAspectChanged(final EcsEntity entity) {
        if (isUpdating()) {
            invokeLater(() -> onEntityAspectChangedInternal(entity));
        } else {
            onEntityAspectChangedInternal(entity);
        }
    }

    private void onEntityAspectChangedInternal(final EcsEntity entity) {
        for (final EcsFamily family : familyByAspect.values()) {
            family.onEntityAspectChanged(entity);
        }
    }

    public void update() {
        if (updating.compareAndSet(false, true)) {
            try {
                executePendingTasks();
                updateSystems();
            } finally {
                updating.set(false);
            }
        }
    }

    private void executePendingTasks() {
        final Runnable[] tasks;
        synchronized (pendingTasks) {
            tasks = pendingTasks.toArray(new Runnable[0]);
            pendingTasks.clear();
        }
        for (final Runnable task : tasks) {
            try {
                task.run();
            } catch (final RuntimeException exc) {
                System.err.println("unhandled runtime-exception caught:");
                exc.printStackTrace();
            }
        }
    }

    private void updateSystems() {
        for (final EcsWrapper wrapper : wrappers) {
            try {
                wrapper.update(this);
            } catch (final RuntimeException exc) {
                System.err.println("unhandled runtime-exception caught:");
                exc.printStackTrace();
            }
        }
    }
}
