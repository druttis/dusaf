package org.dru.dusaf.time;


import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.inject.Module;

import javax.inject.Singleton;

public final class TimeModule implements Module {
    public TimeModule() {
    }

    @Provides
    @Singleton
    @Expose
    public TimeSupplier getTimeSupplier() {
        return new TimeSupplierImpl();
    }
}
