package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;
import org.dru.dusaf.database.model.DbTable;
import org.dru.dusaf.util.CollectionUtils;

import java.util.Objects;
import java.util.stream.Collectors;

public final class SQLInsertBuilder extends SQLBuilder {
    private final DbTable table;

    public SQLInsertBuilder(final DbTable table) {
        Objects.requireNonNull(table, "table");
        this.table = table;
    }

    public SQLInsertBuilder column(final DbColumn<?> column) {
        addColumn(column);
        addParameter(SQL.value(column));
        return this;
    }

    public SQLInsertBuilder columns(final DbColumn<?> first, final DbColumn<?>... rest) {
        CollectionUtils.asList(first, rest).forEach(this::column);
        return this;
    }

    @Override
    protected String getSQL() {
        return "INSERT INTO " +
                table.getDbName() +
                " (" +
                getColumns().stream().map(DbColumn::getDbName).collect(Collectors.joining(",")) +
                ") VALUES (" +
                getParameters().stream().map(SQLParameter::getSQL).collect(Collectors.joining(",")) +
                ")";
    }

    public SQL build() {
        return new SQL(getSQL(), getColumns(), getParameters(), getConditions());
    }
}
