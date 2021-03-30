package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;
import org.dru.dusaf.database.model.DbTable;
import org.dru.dusaf.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SQL {
    public static SQLSelectBuilder select(final DbColumn<?> first, final DbColumn<?>... rest) {
        return new SQLSelectBuilder(CollectionUtils.asList(first, rest));
    }

    public static SQLInsertBuilder insertInto(final DbTable table) {
        return new SQLInsertBuilder(table);
    }

    public static SQLUpdateBuilder update(final DbTable table) {
        return new SQLUpdateBuilder(table);
    }

    public static SQLDeleteBuilder deleteFrom(final DbTable table) {
        return new SQLDeleteBuilder(table);
    }

    public static <T> SQLValue<T> value(final DbColumn<T> column) {
        return new SQLValue<>(column);
    }

    public static <T> SQLEq<T> eq(final DbColumn<T> column) {
        return new SQLEq<>(column);
    }

    public static <T> SQLIn<T> in(final DbColumn<T> column, final int length) {
        return new SQLIn<>(column, length);
    }

    public static <T> SQLIn<T> in(final DbColumn<T> column, final Collection<T> args) {
        Objects.requireNonNull(args, "values");
        return new SQLIn<>(column, args.size());
    }

    private final String sql;
    private final Map<DbColumn<?>, Integer> indexByColumn;
    private final Map<DbColumn<?>, List<SQLParameter<?>>> parametersByColumn;

    public SQL(final String sql, final List<DbColumn<?>> columns, final List<SQLParameter<?>> parameters,
               final List<SQLCondition<?>> conditions) {
        Objects.requireNonNull(sql, "sql");
        this.sql = sql;
        indexByColumn = createIndexByColumn(columns);
        parametersByColumn = createParametersByColumn(parameters, conditions);
    }

    public String getSQL() {
        return sql;
    }

    public PreparedStatement prepareStatement(final Connection conn) throws SQLException {
        return conn.prepareStatement(getSQL());
    }

    private Map<DbColumn<?>, Integer> createIndexByColumn(final List<DbColumn<?>> columns) {
        return IntStream.range(0, columns.size()).boxed().collect(Collectors.toMap(columns::get, index -> (index + 1)));
    }

    private Map<DbColumn<?>, List<SQLParameter<?>>> createParametersByColumn(final List<SQLParameter<?>> parameters,
                                                                             final List<SQLCondition<?>> conditions) {
        final Map<DbColumn<?>, List<SQLParameter<?>>> result = new HashMap<>();
        final AtomicInteger index = new AtomicInteger(1);
        parameters.forEach(parameter -> {
            final List<SQLParameter<?>> list = result.computeIfAbsent(parameter.getColumn(), $ -> new ArrayList<>());
            list.add(parameter);
            parameter.setIndex(index.getAndIncrement());
        });
        conditions.forEach(condition -> {
            final List<SQLParameter<?>> list = result.computeIfAbsent(condition.getColumn(), $ -> new ArrayList<>());
            list.add(condition);
            condition.setIndex(index.getAndIncrement());
        });
        return result;
    }

    public <T> T get(final ResultSet rset, final DbColumn<T> column) throws SQLException {
        final int index = indexByColumn.getOrDefault(column, -1);
        if (index == -1) {
            throw new IllegalArgumentException("no such column: " + column.getName());
        }
        return column.get(rset, index);
    }

    public <T> void set(final PreparedStatement stmt, final DbColumn<T> column, final int ordinal,
                        final T value) throws SQLException {
        final SQLParameter<?> parameter = getParameter(column, ordinal);
        if (parameter.length() > 1) {
            throw new IllegalArgumentException("array parameter");
        }
        column.set(stmt, parameter.getIndex(), value);
    }

    public <T> void set(final PreparedStatement stmt, final DbColumn<T> column, final int ordinal,
                        final Collection<T> values) throws SQLException {
        final SQLParameter<?> parameter = getParameter(column, ordinal);
        if (values.size() != parameter.length()) {
            throw new IllegalArgumentException("values mismatch the length of parameter");
        }
        int index = parameter.getIndex();
        for (final T value : values) {
            column.set(stmt, index, value);
            index++;
        }
    }

    private SQLParameter<?> getParameter(final DbColumn<?> column, final int ordinal) {
        final List<SQLParameter<?>> parameters = parametersByColumn.get(column);
        if (parameters == null) {
            throw new IllegalArgumentException("no such parameter: " + column.getName());
        }
        return parameters.get(ordinal);
    }

    @Override
    public String toString() {
        return "SQL{" +
                "sql='" + sql + '\'' +
                ", indexByColumn=" + indexByColumn +
                ", parametersByColumn=" + parametersByColumn +
                '}';
    }
}
