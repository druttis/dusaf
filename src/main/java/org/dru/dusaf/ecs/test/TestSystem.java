package org.dru.dusaf.ecs.test;

import org.dru.dusaf.ecs.*;
import org.dru.dusaf.inject.InjectionBuilder;
import org.dru.dusaf.inject.Injector;

import java.awt.*;

public class TestSystem implements EcsSystem {
    public static final int NUM_ENTITIES = 1000000;
    public static final int NUM_ITERATIONS = 100;

    private EcsMapping<Point> pointMapping;
    private EcsMapping<Dimension> dimensionMapping;

    @Override
    public void map(final EcsMappings mappings) {
        pointMapping = mappings.getMapping(Point.class);
        dimensionMapping = mappings.getMapping(Dimension.class);
    }

    @Override
    public void bind(final EcsBinder binder) {
        binder.bindAllOf(pointMapping, dimensionMapping);
    }

    @Override
    public void initialize(final EcsEngine engine) {
        for (int i = 0; i < NUM_ENTITIES; i++) {
            final EcsEntity entity = engine.spawnEntity();
            pointMapping.setComponent(entity, new Point(0, 0));
            dimensionMapping.setComponent(entity, new Dimension(0, 0));
        }
    }

    @Override
    public void update(final EcsEngine engine, final Iterable<EcsEntity> entities) {
        entities.forEach(entity -> {
            pointMapping.getComponent(entity).x++;
            dimensionMapping.getComponent(entity).width++;
        });
    }

    @Override
    public void destroy(final EcsEngine engine) {

    }

    public static void main(String[] args) {
        final Injector injector = InjectionBuilder.newInjector(EcsModule.class);
        final EcsEngineBuilderSupplier supplier = injector.getInstance(EcsEngineBuilderSupplier.class);
        final EcsEngineBuilder builder = supplier.get();
        builder.withDynamicMapping();
        /*
        builder.withMapping(Point.class, 0);
        builder.withMapping(Dimension.class, 1);
         */
        System.out.printf("number of entities    : %d\n", NUM_ENTITIES);
        final EcsEngine engine = builder.build();
        engine.addSystem(new TestSystem());
        System.out.printf("number of iterations  : %d\n", NUM_ITERATIONS);
        final long startNanos = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            engine.update();
        }
        final long endNanos = System.nanoTime();
        final double totalNanos = endNanos - startNanos;
        final double nanosPerIteration = totalNanos / NUM_ITERATIONS;
        final double nanosPerEntity = nanosPerIteration / NUM_ENTITIES;
        System.out.println("------------------------------------");
        System.out.printf("total time            : %.0f ms.\n", totalNanos / 1000000d);
        System.out.printf("time per iteration    : %.2f ms.\n", nanosPerIteration / 1000000d);
        System.out.printf("time per entity       : %.5f ns.\n", nanosPerEntity);
    }
}
