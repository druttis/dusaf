package org.dru.dusaf.inject;

import javax.inject.Provider;

public interface Binding<T> {
    Key<T> getKey();

    boolean isExposed();

    Class<? extends Provider<? extends T>> getProviderClass();

    Class<? extends Scoping> getScopeClass();
}
