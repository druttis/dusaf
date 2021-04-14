package org.dru.dusaf.database.type;

public interface DbTypes {
    <T> DbType<T> getType(Class<T> type, int length);

    <T> DbType<T> getType(Class<T> type);
}
