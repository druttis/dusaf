package org.dru.dusaf.ecs.test;

import org.dru.dusaf.ecs.*;

import java.awt.*;

public class TestSystem implements EcsSystem {
    private EcsMapping<Point> pointMapping;

    @Override
    public void map(final EcsMappings mappings) {
        pointMapping = mappings.getMapping(Point.class);
    }

    @Override
    public void bind(final EcsBinder binder) {
        binder.bindAnyOf(pointMapping);
    }

    @Override
    public void initialize(final EcsEngine engine) {
        final EcsEntity entity = engine.spawnEntity();
        pointMapping.setComponent(entity, new Point(0, 100));
    }

    @Override
    public void update(final EcsEngine engine, final Iterable<EcsEntity> entities) {
        entities.forEach(entity -> {
            pointMapping.getComponent(entity).x++;
            System.out.println(pointMapping.getComponent(entity));
        });
    }

    @Override
    public void destroy(final EcsEngine engine) {

    }

    public static void main(String[] args) {
        final EcsEngine engine = new EcsEngine();
        engine.addSystem(new TestSystem());
        for (int i = 0; i < 100; i++) {
            engine.update();
        }
    }
}
