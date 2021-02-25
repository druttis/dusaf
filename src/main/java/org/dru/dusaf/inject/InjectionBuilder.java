package org.dru.dusaf.inject;

import org.dru.dusaf.inject.internal.InjectionImpl;
import org.dru.dusaf.inject.internal.ScopingFactoryRegistry;
import org.dru.dusaf.inject.internal.SingletonScopingFactory;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;

@SuppressWarnings("UnusedReturnValue")
public final class InjectionBuilder {
    public static Injector newInjector(final Class<? extends Module> moduleType) {
        return new InjectionBuilder().build().getInjector(moduleType);
    }

    private final ScopingFactoryRegistry scopingFactoryRegistry;

    public InjectionBuilder() {
        scopingFactoryRegistry = new ScopingFactoryRegistry();
        withScopingFactory(Singleton.class, new SingletonScopingFactory());
    }

    public <T extends Annotation> InjectionBuilder withScopingFactory(final Class<T> annotationType,
                                                                      final ScopingFactory<T> scopingFactory) {
        scopingFactoryRegistry.registerScopingFactory(annotationType, scopingFactory);
        return this;
    }

    public Injection build() {
        return new InjectionImpl(scopingFactoryRegistry);
    }
}
