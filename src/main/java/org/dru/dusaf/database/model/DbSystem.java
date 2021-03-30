package org.dru.dusaf.database.model;

public interface DbSystem {
    DbTemplate newTemplate(String name);

    <T> DbVariable<T> newVariable(Class<T> type);
}
