package org.dru.dusaf.database.model;

import org.dru.dusaf.database.type.DbType;

public interface DbContext {
    <T> DbType<T> getDbType(Class<T> type);
}
