package org.dru.dusaf.database;

import org.dru.dusaf.cache.CacheModule;
import org.dru.dusaf.database.config.DbClusterConfig;
import org.dru.dusaf.database.executor.DbExecutorProvider;
import org.dru.dusaf.database.executor.DbExecutorProviderImpl;
import org.dru.dusaf.database.model.DbSystem;
import org.dru.dusaf.database.model.DbSystemImpl;
import org.dru.dusaf.database.model.DbTableManager;
import org.dru.dusaf.database.model.DbTableManagerImpl;
import org.dru.dusaf.database.pool.DbPoolManager;
import org.dru.dusaf.database.pool.DbPoolManagerImpl;
import org.dru.dusaf.database.store.DbStoreFactory;
import org.dru.dusaf.database.store.DbStoreFactoryImpl;
import org.dru.dusaf.database.type.DbTypes;
import org.dru.dusaf.database.type.DbTypesImpl;
import org.dru.dusaf.inject.DependsOn;
import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.json.JsonModule;
import org.dru.dusaf.json.JsonSerializerSupplier;
import org.dru.dusaf.json.conf.JsonConf;
import org.dru.dusaf.time.TimeModule;
import org.dru.dusaf.time.TimeSupplier;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

@DependsOn({CacheModule.class, JsonModule.class, TimeModule.class})
public final class DatabaseModule implements Module {
    @Provides
    @Singleton
    @Expose
    public DbPoolManager getDbPoolManager(final TimeSupplier timeSupplier) {
        return new DbPoolManagerImpl(timeSupplier);
    }

    @Provides
    @Singleton
    @Expose
    public DbExecutorProvider getDbExecutorProvider(final DbPoolManager dbPoolManager) {
        return new DbExecutorProviderImpl(dbPoolManager);
    }

    @Provides
    @Singleton
    @Expose
    public DbTypes getDbTypes(final JsonSerializerSupplier jsonSerializerSupplier) {
        return new DbTypesImpl(jsonSerializerSupplier);
    }

    @Provides
    @Singleton
    @Expose
    public DbSystem getTableFactory(final DbTypes dbTypes) {
        return new DbSystemImpl(dbTypes);
    }

    @Provides
    @Singleton
    @Expose
    public DbTableManager getDbTableManager(final DbTypes dbTypes) {
        return new DbTableManagerImpl();
    }

    @Provides
    @Singleton
    @Expose
    public DbStoreFactory getDbStoreFactory(final DbExecutorProvider dbExecutorProvider,
                                            final TimeSupplier timeSupplier,
                                            final DbSystem dbSystem,
                                            final DbTableManager dbTableManager) {
        return new DbStoreFactoryImpl(dbExecutorProvider, timeSupplier, dbSystem, dbTableManager);
    }

    @Inject
    public void confDatabase(final DbPoolManager dbPoolManager, final JsonConf jsonConf) {
        Stream.of(jsonConf.get(DbClusterConfig[].class, "dusaf-database")).forEach(dbPoolManager::addConfig);
    }
}
