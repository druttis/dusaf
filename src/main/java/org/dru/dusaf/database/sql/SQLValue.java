package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;

public final class SQLValue<T> extends SQLParameter<T> {
    public SQLValue(final DbColumn<T> column) {
        super(column, 1);
    }

    @Override
    public String getSQL() {
        return "?";
    }
}
