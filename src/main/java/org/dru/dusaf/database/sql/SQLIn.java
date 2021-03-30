package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SQLIn<T> extends SQLCondition<T> {
    public SQLIn(final DbColumn<T> column, final int length) {
        super(column, length);
    }

    @Override
    public String getSQL() {
        return String.format("%s IN (%s)", getColumn().getDbName(),
                IntStream.range(0, length()).mapToObj($ -> "?").collect(Collectors.joining(",")));
    }
}
