package net.drapuria.framework.database.orm;

@SuppressWarnings("serial")

/**
 * Generic unchecked database exception.
 */
public class SqlDatabaseException extends RuntimeException {

    private String sql;

    public SqlDatabaseException() {}

    public SqlDatabaseException(String msg) {
        super(msg);
    }

    public SqlDatabaseException(Throwable t) {
        super(t);
    }

    public SqlDatabaseException(String msg, Throwable t) {
        super(msg, t);
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

}