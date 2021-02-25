package org.dru.dusaf.concurrent.task;

public interface TaskManager extends TaskExecutorFactory {
    TaskExecutor getExecutor(String name);
}
