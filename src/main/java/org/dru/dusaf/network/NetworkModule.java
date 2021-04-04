package org.dru.dusaf.network;

import org.dru.dusaf.concurrent.ConcurrentModule;
import org.dru.dusaf.concurrent.task.TaskManager;
import org.dru.dusaf.inject.DependsOn;
import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.network.internal.NetworkServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

@DependsOn(ConcurrentModule.class)
public final class NetworkModule implements Module {
    @Inject
    public void startup(final TaskManager taskManager) {
        taskManager.newExecutor("network-service", 1);
    }

    @Provides
    @Singleton
    @Expose
    public NetworkService getNetworkService(final TaskManager taskManager) {
        return new NetworkServiceImpl(taskManager);
    }
}
