package org.dru.dusaf.database.model;

import org.dru.dusaf.database.type.DbTypes;

import java.util.Objects;

public final class DbSystemImpl implements DbSystem {
    private final DbTypes dbTypes;

    public DbSystemImpl(final DbTypes dbTypes) {
        this.dbTypes = dbTypes;
    }

    @Override
    public DbTemplate newTemplate(final String name) {
        Objects.requireNonNull(name, "name");
        return new DbTemplateImpl(dbTypes, name);
    }

    @Override
    public <T> DbVariable<T> newVariable(final Class<T> type) {
        return new DbVariableImpl<>(dbTypes, type);
    }
}
