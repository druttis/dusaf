package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;
import org.dru.dusaf.database.model.DbTable;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SQLSelectBuilder extends SQLBuilder {
    private DbTable table;
    private Integer offset;
    private Integer limit;
    private boolean forUpdate;

    public SQLSelectBuilder(final Collection<DbColumn<?>> columns) {
        addColumns(columns);
    }

    @Override
    protected String getSQL() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(getColumns().stream()
                .map(DbColumn::getDbName)
                .collect(Collectors.joining(",")));
        sb.append(" FROM ");
        sb.append(table.getDbName());
        appendConditionSQL(sb);
        if (limit != null) {
            sb.append(" LIMIT ");
            sb.append(limit);
        }
        if (offset != null) {
            sb.append(" OFFSET ");
            sb.append(offset);
        }
        if (forUpdate) {
            sb.append(" FOR UPDATE");
        }
        return sb.toString();
    }

    public SQLSelectBuilder from(final DbTable table) {
        Objects.requireNonNull(table, "table");
        if (this.table != null) {
            throw new IllegalStateException("table already set");
        }
        this.table = table;
        return this;
    }

    public SQLSelectBuilder where(final SQLCondition<?> condition) {
        addCondition(condition);
        return this;
    }

    public SQLSelectBuilder offset(final int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("negative offset");
        }
        if (this.offset != null) {
            throw new IllegalStateException("offset already set");
        }
        this.offset = offset;
        return this;
    }

    public SQLSelectBuilder limit(final int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("negative limit");
        }
        if (this.limit != null) {
            throw new IllegalStateException("limit already set");
        }
        this.limit = limit;
        return this;
    }

    public SQLSelectBuilder forUpdate() {
        if (forUpdate) {
            throw new IllegalStateException("forUpdate already set");
        }
        forUpdate = true;
        return this;
    }

    public SQL build() {
        return new SQL(getSQL(), getColumns(), getParameters(), getConditions());
    }
}
