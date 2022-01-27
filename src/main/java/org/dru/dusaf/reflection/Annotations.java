package org.dru.dusaf.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Annotations {
    public static String toString(final Class<? extends Annotation> annotationType, final Map<Method, Object> members)
            throws Exception {
        final StringBuilder sb = new StringBuilder("@").append(annotationType.getName()).append('(');
        sb.append(Arrays.stream(annotationType.getDeclaredMethods())
                .map(method -> {
                    final String value = Arrays.deepToString(asArray(members.get(method)));
                    return String.format("%s=%s", method.getName(), value.substring(1, value.length() - 1));
                })
                .collect(Collectors.joining(", "))
        );
        return sb.append(')').toString();
    }

    public static int hashCode(final Class<? extends Annotation> annotationType, final Map<Method, Object> members)
            throws Exception {
        int result = 0;
        for (Method method : annotationType.getDeclaredMethods()) {
            result += (127 * method.getName().hashCode()) ^ (Arrays.deepHashCode(asArray(members.get(method))) - 31);
        }
        return result;
    }

    public static boolean equals(final Class<? extends Annotation> annotationType, final Map<Method, Object> members,
                                 final Object other) throws Exception {
        if (!annotationType.isInstance(other)) {
            return false;
        }
        for (final Method method : annotationType.getDeclaredMethods()) {
            if (!Arrays.deepEquals(asArray(method.invoke(other)), asArray(members.get(method)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Keys.of(Point.class).with(Annotations.of(Source.class).with("value", Module.class).build()).build();
     *
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> Builder<A> of(final Class<A> annotationType) {
        return new Builder<>(annotationType);
    }

    private static Object[] asArray(final Object value) {
        return Stream.of(value).toArray();
    }

    private Annotations() {
    }

    public static class Builder<A extends Annotation> {
        private final Class<A> annotationType;
        private final Map<Method, Object> members;

        private Builder(final Class<A> annotationType) {
            this.annotationType = annotationType;
            members = new HashMap<>();
        }

        public Builder<A> with(final String name, final Object value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            final Method method;
            try {
                method = annotationType.getDeclaredMethod(name);
            } catch (final NoSuchMethodException exc) {
                throw new RuntimeException(exc);
            }
            final Class<?> returnType = method.getReturnType();
            if (!returnType.isInstance(value)) {
                throw new IllegalArgumentException(String.format("%s is not an instance of %s", value, returnType));
            }
            if (members.containsKey(method)) {
                throw new IllegalArgumentException(String.format("%s already a member", name));
            }
            members.put(method, value);
            return this;
        }

        public Builder<A> with(final Object value) {
            return with("value", value);
        }

        public A build() {
            Stream.of(annotationType.getDeclaredMethods())
                    .filter(method -> !members.containsKey(method))
                    .filter(method -> method.getDefaultValue() != null)
                    .forEach(method -> members.put(method, method.getDefaultValue()));
            final Set<String> missing = Stream.of(annotationType.getDeclaredMethods())
                    .filter(method -> !members.containsKey(method))
                    .map(Method::getName)
                    .collect(Collectors.toSet());
            if (!missing.isEmpty()) {
                throw new RuntimeException("missing members: " + missing);
            }
            try {
                return annotationType.cast(
                        Proxy.newProxyInstance(
                                annotationType.getClassLoader(),
                                Stream.of(annotationType).toArray(Class[]::new),
                                new Handler(annotationType, members)
                        )
                );
            } catch (final Exception exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    private static final class Handler extends HashMap<Method, Object> implements InvocationHandler {
        private final Class<? extends Annotation> annotationType;
        private final int hashCode;

        private Handler(final Class<? extends Annotation> annotationType, final Map<? extends Method, ?> m)
                throws Exception {
            super(m);
            this.annotationType = annotationType;
            hashCode = Annotations.hashCode(annotationType, this);
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final Object value = get(method);
            if (value != null) {
                return value;
            }
            final String name = method.getName();
            switch (name) {
                case "annotationType":
                    return annotationType;
                case "toString":
                    return Annotations.toString(annotationType, this);
                case "hashCode":
                    return hashCode;
                case "equals":
                    return Annotations.equals(annotationType, this, args[0]);
                default:
                    throw new NoSuchMethodError(name);
            }
        }
    }
}
