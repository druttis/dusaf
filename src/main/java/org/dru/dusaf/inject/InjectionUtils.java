package org.dru.dusaf.inject;

import org.dru.dusaf.reflection.ReflectionUtils;

import javax.inject.Inject;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InjectionUtils {
    public static List<Annotation> getScopeAnnotations(final AnnotatedElement element) {
        return ReflectionUtils.getAnnotationsAnnotatedWith(element, Scope.class);
    }

    public static Annotation getScopeAnnotation(final AnnotatedElement element) {
        final List<Annotation> annotations = getScopeAnnotations(element);
        if (annotations.isEmpty()) {
            return null;
        }
        if (annotations.size() == 1) {
            return annotations.get(0);
        }
        throw new ScopeException("multiple scope annotations: %s", annotations);
    }

    public static List<Class<? extends Module>> getDependencyTypes(final Class<? extends Module> moduleType) {
        Objects.requireNonNull(moduleType, "moduleType");
        final DependsOn dependsOn = moduleType.getAnnotation(DependsOn.class);
        return (dependsOn != null ? Arrays.asList(dependsOn.value()) : Collections.emptyList());
    }

    public static void checkModuleCircularity(final Class<? extends Module> moduleType) {
        Objects.requireNonNull(moduleType, "moduleType");
        checkModuleCircularity(moduleType, moduleType, new HashSet<>());
    }

    private static void checkModuleCircularity(final Class<? extends Module> moduleType,
                                               final Class<? extends Module> currentType,
                                               final Set<Class<? extends Module>> visitedTypes) {
        if (visitedTypes.add(currentType)) {
            for (final Class<? extends Module> dependencyType : getDependencyTypes(currentType)) {
                if (dependencyType.equals(moduleType)) {
                    throw new DependencyException("circular dependency: %s <-> %s",
                            moduleType.getName(), currentType.getName());
                }
                checkModuleCircularity(moduleType, dependencyType, visitedTypes);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getInjectableConstructor(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        final List<Constructor<T>> constructors = Stream.of(type.getDeclaredConstructors())
                .map((constructor) -> (Constructor<T>) constructor)
                .collect(Collectors.toList());
        if (constructors.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s has no constructors...", type.getName()));
        }
        if (constructors.size() == 1) {
            return constructors.get(0);
        }
        constructors.removeIf((constructor) -> constructor.getAnnotation(Inject.class) == null);
        if (constructors.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s has several constructors of which none is"
                    + " %s annotated", type.getName(), Inject.class.getName()));
        }
        if (constructors.size() == 1) {
            return constructors.get(0);
        }
        throw new IllegalArgumentException(String.format("%s has several constructors that is %s annotated",
                type.getName(), Inject.class.getName()));
    }

    private InjectionUtils() throws InstantiationException {
        throw new InstantiationException();
    }
}
