package org.dru.dusaf.ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

final class DefaultEngineBuilder implements EcsEngineBuilder {
    private final Map<Class<?>, Integer> componentIdByType;
    private final Map<Integer, Class<?>> typeByComponentId;
    private EcsMappings mappings;

    DefaultEngineBuilder() {
        componentIdByType = new HashMap<>();
        typeByComponentId = new HashMap<>();
    }

    @Override
    public EcsEngineBuilder3 withDynamicMapping() {
        mappings = new DynamicMappings();
        return this;
    }

    @Override
    public EcsEngineBuilder2 withMapping(final Class<?> type, final int id) {
        componentIdByType.compute(type, ($, old) -> {
            if (old != null) {
                throw new IllegalArgumentException("already bound: type=" + type);
            }
            if (typeByComponentId.putIfAbsent(id, type) != null) {
                throw new IllegalArgumentException("already bound: id=" + id);
            }
            return id;
        });
        return this;
    }

    @Override
    public EcsEngine build() {
        if (mappings == null) {
            mappings = new StaticMappings(componentIdByType.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new EcsMapping<>(e.getValue(), e.getKey()))));
        }
        return new EcsEngine(mappings);
    }
}
