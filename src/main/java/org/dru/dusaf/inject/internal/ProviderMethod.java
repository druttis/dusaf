package org.dru.dusaf.inject.internal;

import javax.inject.Provider;
import java.lang.reflect.Method;
import java.util.function.Supplier;

final class ProviderMethod<T> implements Provider<T> {
    private final InjectorImpl injector;
    private final Supplier<?> supplier;
    private final Method method;

    ProviderMethod(final InjectorImpl injector, final Supplier<?> supplier, final Method method) {
        this.injector = injector;
        this.supplier = supplier;
        this.method = method;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        final T instance = (T) injector.injectMethod(supplier.get(), method);
        injector.injectMembers(instance);
        return instance;
    }

    @Override
    public String toString() {
        return method.toGenericString();
    }
}
