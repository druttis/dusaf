package org.dru.dusaf.database.model;

public interface DbTemplate {
    <T> DbColumn<T> newColumn(String name, Class<T> type, int capacity, DbModifier modifier);

    <T> DbColumn<T> newColumn(String name, Class<T> type, DbModifier modifier);

    <T> DbColumn<T> newColumn(String name, Class<T> type, int capacity);

    <T> DbColumn<T> newColumn(String name, Class<T> type);

    DbTable build();
}
