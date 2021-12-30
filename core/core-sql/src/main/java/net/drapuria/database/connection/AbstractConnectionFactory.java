package net.drapuria.database.connection;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractConnectionFactory {

    public abstract void init();

    public abstract void connect() throws SQLException;

    public abstract void shutdown() throws Exception;

    public abstract Connection getConnection() throws SQLException;


}
