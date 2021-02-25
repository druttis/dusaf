package org.dru.dusaf.database.pool;

import org.dru.dusaf.database.config.DbClusterConfig;

import java.util.List;

public interface DbPoolManager {
    List<DbPool> getPools(String clusterName);

    DbPool getPool(String clusterName, final int shard);

    void addConfig(DbClusterConfig config);
}
