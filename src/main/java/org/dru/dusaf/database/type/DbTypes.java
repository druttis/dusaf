package org.dru.dusaf.database.type;

public interface DbTypes {
    <T> void registerDbType(final Class<T> type, final DbType<T> dbType);

    <T> DbType<T> getDbType(Class<T> type);
}
