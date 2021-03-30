package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbTable;

import java.util.Objects;

public final class SQLDeleteBuilder extends SQLBuilder {
    private final DbTable table;

    public SQLDeleteBuilder(final DbTable table) {
        Objects.requireNonNull(table, "table");
        this.table = table;
    }

    public SQLDeleteBuilder where(final SQLCondition<?> condition) {
        addCondition(condition);
        return this;
    }

    @Override
    protected String getSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(table.getDbName());
        appendConditionSQL(sb);
        return sb.toString();
    }

    public SQL build() {
        return new SQL(getSQL(), getColumns(), getParameters(), getConditions());
    }
}
