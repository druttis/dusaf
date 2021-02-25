package org.dru.dusaf.database.model;

import org.dru.dusaf.database.executor.DbExecutor;

public interface DbTableManager {
    void createTableIfNotExist(DbExecutor executor, int shard, DbTable<?> table);
}
