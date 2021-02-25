package org.dru.dusaf.concurrent;

import org.dru.dusaf.concurrent.task.TaskExecutorFactory;
import org.dru.dusaf.concurrent.task.TaskExecutorFactoryImpl;
import org.dru.dusaf.concurrent.task.TaskManager;
import org.dru.dusaf.concurrent.task.TaskManagerImpl;
import org.dru.dusaf.event.EventBus;
import org.dru.dusaf.event.EventModule;
import org.dru.dusaf.inject.DependsOn;
import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.time.TimeModule;
import org.dru.dusaf.time.TimeSupplier;

import javax.inject.Singleton;

@DependsOn({EventModule.class, TimeModule.class})
public final class ConcurrentModule implements Module {
    public ConcurrentModule() {
    }

    @Provides
    @Singleton
    @Expose
    public TaskExecutorFactory getTaskExecutorFactory(final EventBus eventBus, final TimeSupplier timeSupplier) {
        return new TaskExecutorFactoryImpl(eventBus, timeSupplier);
    }

    @Provides
    @Singleton
    @Expose
    public TaskManager getTaskManager(final TaskExecutorFactory taskExecutorFactory) {
        return new TaskManagerImpl(taskExecutorFactory);
    }
}
