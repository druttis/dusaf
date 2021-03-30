package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;

public abstract class SQLCondition<T> extends SQLParameter<T> {
    public SQLCondition(final DbColumn<T> column, final int length) {
        super(column, length);
    }
}
