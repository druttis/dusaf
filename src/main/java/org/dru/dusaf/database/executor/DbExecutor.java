package org.dru.dusaf.database.executor;

import org.dru.dusaf.functional.ThrowingConsumer;
import org.dru.dusaf.functional.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbExecutor {
    <T> T query(int shard, ThrowingFunction<Connection, T, SQLException> command) throws SQLException;

    <T> T update(int shard, ThrowingFunction<Connection, T, SQLException> command) throws SQLException;

    void update(int shard, ThrowingConsumer<Connection, SQLException> command) throws SQLException;
}
