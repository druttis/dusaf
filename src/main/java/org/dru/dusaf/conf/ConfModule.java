package org.dru.dusaf.conf;

import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;

import javax.inject.Singleton;

public class ConfModule implements Module {
    @Provides
    @Singleton
    @Expose
    public Conf getConf() {
        return new ConfImpl();
    }
}
