package org.dru.dusaf.database.dialect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DbDialectsImpl implements DbDialects {
    private final Map<String, DbDialect> dialectByName;

    public DbDialectsImpl() {
        dialectByName = new ConcurrentHashMap<>();
    }

    @Override
    public void registerDialect(final DbDialect dialect) {
        final String name = dialect.getName();
        if (dialectByName.putIfAbsent(dialect.getName(), dialect) != null) {
            throw new IllegalStateException("dialect already registered: name=" + name);
        }
    }

    @Override
    public DbDialect get(final String name) {
        final DbDialect dialect = dialectByName.get(name);
        if (dialect == null) {
            throw new IllegalStateException("no such dialect: name=" + name);
        }
        return dialect;
    }
}
