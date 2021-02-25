package org.dru.dusaf.inject;

import javax.inject.Provider;

public interface Scoping {
    <T> Provider<T> scope(Provider<T> provider);
}
