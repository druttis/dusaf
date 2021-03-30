package org.dru.dusaf.database.model;

import org.dru.dusaf.database.type.DbType;
import org.dru.dusaf.database.type.DbTypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DbColumnImpl<T> extends AbstractDbObject implements DbColumn<T> {
    private final DbVariable<T> variable;
    private final int length;
    private final DbModifier modifier;

    public DbColumnImpl(final String name, final DbTypes dbTypes, final Class<T> type, final int length,
                        final DbModifier modifier) {
        super(name);
        variable = new DbVariableImpl<>(dbTypes, type);
        this.length = length;
        this.modifier = modifier;
    }

    @Override
    public String getDDL() {
        final StringBuilder sb = new StringBuilder(getDbName());
        sb.append(' ');
        sb.append(getDbType().getDDL(getLength()));
        if (isNotNull()) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }

    @Override
    public Class<T> getType() {
        return variable.getType();
    }

    @Override
    public DbType<T> getDbType() {
        return variable.getDbType();
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isPrimary() {
        return modifier == DbModifier.PRIMARY;
    }

    @Override
    public boolean isNotNull() {
        switch (modifier) {
            case PRIMARY:
            case NOT_NULL:
                return true;
        }
        return false;
    }

    @Override
    public T get(final ResultSet rset, final int columnIndex) throws SQLException {
        return variable.get(rset, columnIndex);
    }

    @Override
    public void set(final PreparedStatement stmt, final int parameterIndex, final T value) throws SQLException {
        variable.set(stmt, parameterIndex, value);
    }

    @Override
    public String toString() {
        return getDbName();
    }
}
