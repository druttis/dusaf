package org.dru.dusaf.database.model;

import org.dru.dusaf.database.type.DbTypes;

import java.util.*;

public final class DbTemplateImpl implements DbTemplate {
    private final DbTypes dbTypes;
    private final String name;
    private final List<DbColumn<?>> columns;
    private final Set<String> columnNames;

    DbTemplateImpl(final DbTypes dbTypes, final String name) {
        this.dbTypes = dbTypes;
        this.name = name;
        columns = new ArrayList<>();
        columnNames = new HashSet<>();
    }

    @Override
    public <T> DbColumn<T> newColumn(final String name, final Class<T> type, final int length,
                                     final DbModifier modifier) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        if (length < 0) {
            throw new IllegalArgumentException("negative length: " + length);
        }
        Objects.requireNonNull(modifier, "modifier");
        if (!columnNames.add(name)) {
            throw new IllegalArgumentException("column already exist: " + name);
        }
        final DbColumn<T> column = new DbColumnImpl<>(name, dbTypes, type, length, modifier);
        columns.add(column);
        return column;
    }

    @Override
    public <T> DbColumn<T> newColumn(final String name, final Class<T> type, final DbModifier modifier) {
        return newColumn(name, type, 0, modifier);
    }

    @Override
    public <T> DbColumn<T> newColumn(final String name, final Class<T> type, final int length) {
        return newColumn(name, type, length, DbModifier.NONE);
    }

    @Override
    public <T> DbColumn<T> newColumn(final String name, final Class<T> type) {
        return newColumn(name, type, 0, DbModifier.NONE);
    }

    @Override
    public DbTable build() {
        return new DbTableImpl(name, new ArrayList<>(columns));
    }

    private void requireLength(final Class<?> type) {
        if (dbTypes.of(type, 0).isVariableLength()) {
            throw new RuntimeException("length required");
        }
    }
}
