package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;
import org.dru.dusaf.database.model.DbTable;
import org.dru.dusaf.util.CollectionUtils;

import java.util.Objects;
import java.util.stream.Collectors;

public final class SQLUpdateBuilder extends SQLBuilder {
    private final DbTable table;

    public SQLUpdateBuilder(final DbTable table) {
        Objects.requireNonNull(table, "table");
        this.table = table;
    }

    public SQLUpdateBuilder set(final DbColumn<?> column) {
        addParameter(SQL.eq(column));
        return this;
    }

    public SQLUpdateBuilder set(final DbColumn<?> first, final DbColumn<?>... rest) {
        CollectionUtils.asList(first, rest).forEach(this::set);
        return this;
    }

    public SQLUpdateBuilder where(final SQLCondition<?> condition) {
        addCondition(condition);
        return this;
    }

    @Override
    protected String getSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(table.getDbName());
        sb.append(" SET ");
        sb.append(getParameters().stream()
                .map(SQLParameter::getSQL)
                .collect(Collectors.joining(",")));
        appendConditionSQL(sb);
        return sb.toString();
    }

    public SQL build() {
        return new SQL(getSQL(), getColumns(), getParameters(), getConditions());
    }
}
