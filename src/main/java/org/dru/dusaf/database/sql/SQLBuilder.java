package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SQLBuilder {
    private final List<DbColumn<?>> columns;
    private final Set<String> columnNames;
    private final List<SQLParameter<?>> parameters;
    private final List<SQLCondition<?>> conditions;

    public SQLBuilder() {
        columns = new ArrayList<>();
        columnNames = new HashSet<>();
        parameters = new ArrayList<>();
        conditions = new ArrayList<>();
    }

    protected final void addColumn(final DbColumn<?> column) {
        Objects.requireNonNull(column, "column");
        if (!columnNames.add(column.getName())) {
            throw new IllegalArgumentException("column already exist: " + column.getName());
        }
        columns.add(column);
    }

    protected final void addColumns(final Collection<DbColumn<?>> columns) {
        Objects.requireNonNull(columns, "columns");
        columns.forEach(this::addColumn);
    }

    protected final void addParameter(final SQLParameter<?> parameter) {
        Objects.requireNonNull(parameter, "parameter");
        parameters.add(parameter);
    }

    protected final void addParameters(final Collection<SQLParameter<?>> parameters) {
        Objects.requireNonNull(parameters, "parameters");
        parameters.forEach(this::addParameter);
    }

    protected final void addCondition(final SQLCondition<?> condition) {
        Objects.requireNonNull(condition, "condition");
        conditions.add(condition);
    }

    protected final void addCondition(final Collection<SQLCondition<?>> conditions) {
        Objects.requireNonNull(conditions, "conditions");
        conditions.forEach(this::addCondition);
    }

    protected final List<DbColumn<?>> getColumns() {
        return columns;
    }

    protected final List<SQLParameter<?>> getParameters() {
        return parameters;
    }

    protected final List<SQLCondition<?>> getConditions() {
        return conditions;
    }

    protected void appendConditionSQL(final StringBuilder sb) {
        if (!conditions.isEmpty()) {
            sb.append(" WHERE ");
            sb.append(conditions.stream().map(SQLCondition::getSQL).collect(Collectors.joining(" AND ")));
        }
    }

    protected abstract String getSQL();
}

