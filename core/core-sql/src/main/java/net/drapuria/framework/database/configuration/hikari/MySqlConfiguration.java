/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.configuration.hikari;

import com.zaxxer.hikari.HikariConfig;
import net.drapuria.framework.database.connection.MySqlConnectionFactory;


public abstract class MySqlConfiguration extends HikariConfiguration<MySqlConnectionFactory>{

    @Override
    public Class<MySqlConnectionFactory> factoryClass() {
        return MySqlConnectionFactory.class;
    }

    @Override
    public void setupFactory(MySqlConnectionFactory factory) {
        super.setupFactory(factory);
        HikariConfig hikariConfig = factory.getConfig();

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("useLocalTransactionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
    }

}
