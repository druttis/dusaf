package org.dru.dusaf.database.dialect;

public interface DbDialects {
    void registerDialect(DbDialect dialect);

    DbDialect get(String name);
}
