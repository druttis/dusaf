package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;

public final class SQLEq<T> extends SQLCondition<T> {
    public SQLEq(final DbColumn<T> column) {
        super(column, 1);
    }

    @Override
    public String getSQL() {
        return String.format("%s=?", getColumn().getDbName());
    }
}
