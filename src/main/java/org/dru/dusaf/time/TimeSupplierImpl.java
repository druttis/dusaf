package org.dru.dusaf.time;

import java.time.Instant;

public final class TimeSupplierImpl implements TimeSupplier {
    public TimeSupplierImpl() {
    }

    @Override
    public Instant get() {
        return Instant.now();
    }
}
