package org.dru.dusaf.database.executor;

public interface DbExecutorProvider {
    DbExecutor getExecutor(String clusterName);
}
