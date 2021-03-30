package org.dru.dusaf.database.sql;

import org.dru.dusaf.database.model.DbColumn;

import java.util.Objects;

public abstract class SQLParameter<T> {
    private final DbColumn<T> column;
    private final int length;
    private int index;

    public SQLParameter(final DbColumn<T> column, final int length) {
        Objects.requireNonNull(column, "column");
        if (length < 1) {
            throw new IllegalArgumentException("length has to be 1 or greater: " + length);
        }
        this.column = column;
        this.length = length;
    }

    public final DbColumn<T> getColumn() {
        return column;
    }

    public final int length() {
        return length;
    }

    public final int getIndex() {
        return index;
    }

    final void setIndex(final int index) {
        this.index = index;
    }

    public abstract String getSQL();

    @Override
    public String toString() {
        return String.valueOf(index);
    }
}
