package org.dru.dusaf.database.model;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbObject {
    String getName();

    String getDbName();

    String getDDL(final Connection conn) throws SQLException;
}
