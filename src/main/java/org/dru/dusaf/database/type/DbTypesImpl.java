package org.dru.dusaf.database.type;

import org.dru.dusaf.database.dialect.DbDialects;

public final class DbTypesImpl implements DbTypes {
    private final DbDialects dialects;

    public DbTypesImpl(final DbDialects dialects) {
        this.dialects = dialects;
    }

    @Override
    public <T> DbType<T> getType(final Class<T> type, final int length) {
        return new DialectalDbType<>(dialects, type, length);
    }

    @Override
    public <T> DbType<T> getType(final Class<T> type) {
        return getType(type, 0);
    }
}
