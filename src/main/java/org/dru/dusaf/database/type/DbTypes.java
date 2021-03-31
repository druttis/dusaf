package org.dru.dusaf.database.type;

public interface DbTypes {
    <T> void register(DbType<T> type);

    <T> DbType<T> of(Class<T> type, int length);
}
