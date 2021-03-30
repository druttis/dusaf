package org.dru.dusaf.database.model;

import java.util.Objects;

public abstract class AbstractDbObject implements DbObject {
    private final String name;
    private final String dbName;

    public AbstractDbObject(final String name) {
        Objects.requireNonNull(name, "name");
        this.name = name;
        dbName = String.format("`%s`", name);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getDbName() {
        return dbName;
    }
}
