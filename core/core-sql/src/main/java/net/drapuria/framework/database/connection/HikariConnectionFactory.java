package net.drapuria.framework.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.database.orm.statement.LegacySqlStatementBuilder;
import net.drapuria.framework.database.orm.statement.MySqlStatementBuilder;
import net.drapuria.framework.database.orm.statement.SqlStatementBuilder;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class HikariConnectionFactory extends AbstractConnectionFactory {

    private HikariConfig config;
    private HikariDataSource dataSource;

    @Override
    public void init() {
        this.config = new HikariConfig();
        this.config.setPoolName("drapuria-hikari");
        this.config.setInitializationFailTimeout(-1);
        this.config.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    @Override
    public void connect() throws SQLException {
        if (this.dataSource != null) {
            FrameworkMisc.PLATFORM.getLogger().error("[Drapuria-Hikari] Hikari Datasource already initialized.");
            return;
        }
        this.dataSource = new HikariDataSource(this.config);
       // this.postInit();
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
        return dataSource.getConnection();
    }

    private void postInit() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals("com.mysql.cj.jdbc.Driver")) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public SqlStatementBuilder builder() {
       return new MySqlStatementBuilder();
    }

    public abstract void configureDatabase(String hostname, String port, String databaseName, String username, String password);

}
