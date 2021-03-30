package org.dru.dusaf.database.store;

import org.dru.dusaf.database.executor.DbExecutor;
import org.dru.dusaf.database.executor.DbExecutorProvider;
import org.dru.dusaf.database.model.DbSystem;
import org.dru.dusaf.database.model.DbTableManager;
import org.dru.dusaf.time.TimeSupplier;

public final class DbStoreFactoryImpl implements DbStoreFactory {
    private static final String CLUSTER_NAME = "store";

    private final DbExecutorProvider dbExecutorProvider;
    private final TimeSupplier timeSupplier;
    private final DbSystem tableBuilderFactory;
    private final DbTableManager tableManager;

    public DbStoreFactoryImpl(final DbExecutorProvider dbExecutorProvider, final TimeSupplier timeSupplier,
                              final DbSystem tableBuilderFactory, final DbTableManager tableManager) {
        this.dbExecutorProvider = dbExecutorProvider;
        this.timeSupplier = timeSupplier;
        this.tableBuilderFactory = tableBuilderFactory;
        this.tableManager = tableManager;
    }

    @Override
    public <K, V> DbStore<K, V> newStore(final String name, final Class<K> keyType, final Class<V> valueType) {
        final DbExecutor dbExecutor = dbExecutorProvider.getExecutor(CLUSTER_NAME);
        return new DbStoreImpl<>(dbExecutor, timeSupplier, name, keyType, valueType, tableBuilderFactory, tableManager);
    }
}
