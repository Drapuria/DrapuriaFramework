package net.drapuria.framework.database.connection;

import com.zaxxer.hikari.HikariConfig;
import net.drapuria.framework.RepositoryType;

public class MySqlConnectionFactory extends HikariConnectionFactory {

    @Override
    public void configureDatabase(String hostname, String port, String databaseName, String username, String password) {
        HikariConfig hikariConfig = this.getConfig();
        hikariConfig.setMaximumPoolSize(10);
      //  hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariConfig.addDataSourceProperty("serverName", hostname);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", databaseName);
        hikariConfig.addDataSourceProperty("user", username);
        hikariConfig.addDataSourceProperty("password", password);
        hikariConfig.setLeakDetectionThreshold(30000);
    }

    @Override
    public RepositoryType type() {
        return RepositoryType.MYSQL;
    }
}
