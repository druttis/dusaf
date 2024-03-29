package org.dru.dusaf.inject.internal;

import org.dru.dusaf.inject.Binding;
import org.dru.dusaf.inject.Injection;
import org.dru.dusaf.inject.InjectionUtils;
import org.dru.dusaf.inject.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public final class InjectionImpl implements Injection {
    private static final Logger logger = LoggerFactory.getLogger(InjectionImpl.class);

    private final ScopingFactoryRegistry scopingFactoryRegistry;
    private final Map<Class<?>, InjectorImpl> injectorImplByModuleType;

    public InjectionImpl(final ScopingFactoryRegistry scopingFactoryRegistry) {
        Objects.requireNonNull(scopingFactoryRegistry, "scopeFactoryRegistry");
        this.scopingFactoryRegistry = scopingFactoryRegistry;
        injectorImplByModuleType = new HashMap<>();
    }

    @Override
    public synchronized List<Binding<?>> getBindings() {
        return injectorImplByModuleType.values().stream()
                .flatMap((injectorImpl) -> injectorImpl.getLocalBindings().stream())
                .collect(Collectors.toList());
    }

    @Override
    public synchronized InjectorImpl getInjector(final Class<? extends Module> moduleType) {
        return getInjectorInternal(moduleType);
    }

    private InjectorImpl getInjectorInternal(final Class<? extends Module> moduleType) {
        InjectorImpl injectorImpl = injectorImplByModuleType.get(moduleType);
        if (injectorImpl == null) {
            InjectionUtils.checkModuleCircularity(moduleType);
            final Collection<Class<? extends Module>> dependencyTypes = InjectionUtils.getDependencyTypes(moduleType);
            dependencyTypes.forEach(this::getInjectorInternal);
            injectorImpl = new InjectorImpl(this, null, scopingFactoryRegistry, moduleType, dependencyTypes);
            injectorImplByModuleType.put(moduleType, injectorImpl);
            final Module moduleInstance = injectorImpl.newInstance(moduleType, false);
            logger.trace("configuring injector for {}", moduleType.getName());
            injectorImpl.setModuleInstance(moduleInstance);
            injectorImpl.bindProviderMethods();
            injectorImpl.injectMembers(moduleInstance);
        }
        return injectorImpl;
    }
}
