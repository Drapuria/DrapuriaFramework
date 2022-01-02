package net.drapuria.framework.database.connection;

import lombok.SneakyThrows;
import net.drapuria.framework.RepositoryType;
import net.drapuria.framework.database.orm.Query;
import net.drapuria.framework.database.orm.Session;
import net.drapuria.framework.database.orm.Transaction;
import net.drapuria.framework.database.orm.statement.SqlStatementBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractConnectionFactory {

    public abstract RepositoryType type();

    public abstract void init();

    public abstract void connect() throws SQLException;

    public abstract void shutdown() throws Exception;

    public abstract Connection getConnection() throws SQLException;

    public abstract SqlStatementBuilder builder();

    public Session session(Transaction transaction) {
        return new Session(this, transaction);
    }

    /**
     * Simple, primitive method for creating a table based on a pojo. Does not add
     * indexes or implement complex data types. Probably not suitable for production
     * use.
     */
    public Query createTable(Class<?> clazz) {
        return new Query(this).createTable(clazz);
    }

    /**
     * Insert a row into a table. The row pojo can have a @Table annotation to
     * specify the table, or you can specify the table with the .table() method.
     */
    public Query insert(Object row) {
        return new Query(this).insert(row);
    }

    /**
     * See {@link Query#generatedKeyReceiver(Object, String...)
     * generateKeyReceiver} method.
     */
    public Query generatedKeyReceiver(Object generatedKeyReceiver, String... generatedKeyNames) {
        return new Query(this).generatedKeyReceiver(generatedKeyReceiver, generatedKeyNames);
    }

    /**
     * Delete a row in a table. This method looks for an @Id annotation to find the
     * row to delete by primary key, and looks for a @Table annotation to figure out
     * which table to hit.
     */
    public Query delete(Object row) {
        return new Query(this).delete(row);
    }

    /**
     * Execute a "select" query and get some results. The system will create a new
     * object of type "clazz" for each row in the result set and add it to a List.
     * It will also try to extract the table name from a @Table annotation in the
     * clazz.
     */
    public <T> List<T> results(Class<T> clazz) {
        return new Query(this).results(clazz);
    }

    /**
     * Returns the first row in a query in a pojo. Will return it in a Map if a
     * class that implements Map is specified.
     */
    public <T> T first(Class<T> clazz) {
        return new Query(this).first(clazz);
    }

    /**
     * Returns the first row in a query in a defined pojo. Will return it in a Map if a
     * class that implements Map is specified
     */
    public <T> boolean first(Class<T> clazz, T t) {
        return new Query(this).first(clazz, t);
    }

    /**
     * Update a row in a table. It will match an existing row based on the primary
     * key.
     */
    public Query update(Object row) {
        return new Query(this).update(row);
    }

    /**
     * Upsert a row in a table. It will insert, and if that fails, do an update with
     * a match on a primary key.
     */
    public Query upsert(Object row) {
        return new Query(this).upsert(row);
    }

    /**
     * Create a query and specify which table it operates on.
     */
    public Query table(String table) {
        return new Query(this).table(table);
    }

    public long count(Class<?> type) {
        return new Query(this).count(type).first(Long.class);
    }

    /**
     * Start a database transaction. Pass the transaction object to each query or
     * command that should be part of the transaction using the .transaction()
     * method. Then call transaction.commit() or .rollback() to complete the
     * process. No need to close the transaction.
     *
     * @return a transaction object
     */
    @SneakyThrows
    public Transaction startTransaction() {
        Transaction trans = new Transaction();
        trans.setConnection(this.getConnection());
        return trans;
    }

    /**
     * Create a query that uses this transaction object.
     */
    public Query transaction(Transaction trans) {
        return new Query(this).transaction(trans);
    }

    public Query query() {
        return new Query(this);
    }

}
