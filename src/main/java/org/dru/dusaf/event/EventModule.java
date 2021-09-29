package org.dru.dusaf.event;

import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;

import javax.inject.Singleton;

public final class EventModule implements Module {
    @Provides
    @Singleton
    @Expose
    public EventBus getEventBus() {
        return new DefaultEventBus();
    }
}
