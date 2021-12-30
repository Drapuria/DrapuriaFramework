package net.drapuria.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnectionFactory extends AbstractConnectionFactory {

    private HikariConfig config;
    private HikariDataSource dataSource;

    @Override
    public void init() {
        this.config = new HikariConfig();

    }

    @Override
    public void connect() throws SQLException {

    }

    @Override
    public void shutdown() throws Exception {
        if (this.dataSource != null && !this.dataSource.isClosed())
            dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }
}
