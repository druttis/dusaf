package org.dru.dusaf.database.model;

import org.dru.dusaf.database.executor.DbExecutor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DbTableManagerImpl implements DbTableManager {
    private final Set<Entry> visited;

    public DbTableManagerImpl() {
        visited = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void createTableIfNotExist(final DbExecutor executor, final int shard, final DbTable table) {
        if (visited.add(new Entry(executor, shard))) {
            try {
                executor.update(shard, conn -> {
                    final String ddl = table.getDDL();
                    System.out.println(ddl);
                    try (final PreparedStatement stmt = conn.prepareStatement(ddl)) {
                        stmt.execute();
                    }
                });
            } catch (final SQLException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    private static final class Entry {
        private final DbExecutor executor;
        private final int shard;

        private Entry(final DbExecutor executor, final int shard) {
            this.executor = executor;
            this.shard = shard;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;
            final Entry entry = (Entry) o;
            return shard == entry.shard &&
                    executor.equals(entry.executor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executor, shard);
        }
    }
}
