package org.dru.dusaf.inject.internal;

import org.dru.dusaf.inject.Scoping;
import org.dru.dusaf.inject.ScopingFactory;

import javax.inject.Singleton;

public final class SingletonScopingFactory implements ScopingFactory<Singleton> {
    @Override
    public Scoping getScoping(final Singleton annotation) {
        return Scopings.SINGLETON;
    }
}
