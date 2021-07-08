package org.dru.dusaf.host;

import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;

import javax.inject.Singleton;

public final class HostModule implements Module {
    @Provides
    @Singleton
    @Expose
    public HostNameProvider getHostNameProvider() {
        return new HostNameProviderImpl();
    }
}
