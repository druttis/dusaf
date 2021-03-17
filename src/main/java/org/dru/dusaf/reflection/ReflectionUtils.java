package org.dru.dusaf.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ReflectionUtils {
    public static List<Annotation> getAnnotationsAnnotatedWith(final AnnotatedElement element,
                                                               final Class<? extends Annotation> annotationType) {
        Objects.requireNonNull(element, "element");
        return Stream.of(element.getAnnotations())
                .filter((annotation) -> annotation.annotationType().isAnnotationPresent(annotationType))
                .collect(Collectors.toList());
    }

    public static <T> List<Class<? super T>> getClassHierarchy(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        final List<Class<? super T>> result = new ArrayList<>();
        Class<? super T> current = type;
        while (current != null) {
            result.add(current);
            current = current.getSuperclass();
        }
        Collections.reverse(result);
        return result;
    }

    public static <T> Constructor<T> getDefaultConstructor(final Class<T> concreteClass) {
        try {
            return concreteClass.getDeclaredConstructor();
        } catch (final NoSuchMethodException exc) {
            throw new IllegalArgumentException(concreteClass + " has no default constructor");
        }
    }

    public static List<Field> getDeclaredFields(final Class<?> type, final Predicate<? super Field> filter) {
        Objects.requireNonNull(filter, "filter");
        return getClassHierarchy(type).stream()
                .flatMap((current) -> Stream.of(current.getDeclaredFields()))
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static List<Field> getDeclaredFields(final Class<?> type) {
        return getDeclaredFields(type, field -> true);
    }

    public static List<Field> getSerializableFields(final Class<?> clazz) {
        return getDeclaredFields(clazz).stream().
                filter(field -> {
                    final int mod = field.getModifiers();
                    return !(Modifier.isTransient(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod));
                }).collect(Collectors.toList());
    }

    public static List<Method> getDeclaredMethods(final Class<?> type, final Predicate<? super Method> filter) {
        Objects.requireNonNull(filter, "filter");
        return getClassHierarchy(type).stream()
                .flatMap((current) -> Stream.of(current.getDeclaredMethods()))
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static List<Method> getDeclaredMethods(final Class<?> type) {
        return getDeclaredMethods(type, method -> true);
    }

    public static <T> T newInstance(final Constructor<T> constructor, final Object... initargs) {
        Objects.requireNonNull(constructor, "constructor");
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(initargs);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException exc) {
            throw new RuntimeException("failed to create new instance: " + constructor.toGenericString(), exc);
        }
    }

    public static Object getFieldValue(final Object object, final Field field) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(field, "field");
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (final IllegalAccessException exc) {
            throw new RuntimeException("failed to get field: " + field.toGenericString(), exc);
        }
    }

    public static void setFieldValue(final Object object, final Field field, final Object value) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(field, "field");
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (final IllegalAccessException exc) {
            throw new RuntimeException("failed to set field: " + field.toGenericString(), exc);
        }
    }

    public static Object invokeMethod(final Object object, final Method method, final Object... args) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(method, "method");
        method.setAccessible(true);
        try {
            return method.invoke(object, args);
        } catch (final IllegalAccessException | InvocationTargetException exc) {
            throw new RuntimeException("failed to invoke method: " + method.toGenericString(), exc);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T copyInstance(final T source) {
        if (source == null) {
            return null;
        }
        final Class<T> objectClass = (Class<T>) source.getClass();
        final Constructor<T> constructor;
        try {
            constructor = objectClass.getDeclaredConstructor();
        } catch (final NoSuchMethodException exc) {
            return source;
        }
        if (objectClass.isPrimitive() || objectClass.equals(String.class)
                || Number.class.isAssignableFrom(objectClass)) {
            return source;
        }
        final T target = newInstance(constructor);
        getDeclaredFields(objectClass).stream().filter(field -> {
            final int mod = field.getModifiers();
            return !Modifier.isStatic(mod);
        }).forEach(field -> setFieldValue(target, field, copyInstance(getFieldValue(source, field))));
        return target;
    }

    private ReflectionUtils() {
    }
}