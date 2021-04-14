package org.dru.dusaf.database.model;

import org.dru.dusaf.functional.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DbTableImpl extends AbstractDbObject implements DbTable {
    private final List<DbColumn<?>> columns;
    private final List<DbColumn<?>> primaryKeyColumns;

    public DbTableImpl(final String name, final List<DbColumn<?>> columns) {
        super(name);
        this.columns = Collections.unmodifiableList(columns);
        primaryKeyColumns = Collections
                .unmodifiableList(columns.stream().filter(DbColumn::isPrimary).collect(Collectors.toList()));
    }

    @Override
    public String getDDL(final Connection conn) throws SQLException {
        final StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(getDbName());
        sb.append(" (\n");
        sb.append(getColumns().stream()
                .map(ThrowingFunction.wrap(dbColumn -> dbColumn.getDDL(conn)))
                .collect(Collectors.joining(",\n")));
        if (!getPrimaryKeyColumns().isEmpty()) {
            sb.append(",\nPRIMARY KEY (");
            sb.append(primaryKeyColumns.stream()
                    .map(DbColumn::getDbName)
                    .collect(Collectors.joining(",")));
            sb.append(')');
        }
        sb.append("\n) ENGINE=InnoDb CHARACTER SET=utf8mb4");
        return sb.toString();
    }

    public List<DbColumn<?>> getColumns() {
        return columns;
    }

    public List<DbColumn<?>> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }
}
