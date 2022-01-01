package net.drapuria.framework.database.configuration.hikari;

import com.zaxxer.hikari.HikariConfig;
import lombok.SneakyThrows;
import net.drapuria.framework.database.configuration.AbstractSqlConfiguration;
import net.drapuria.framework.database.connection.HikariConnectionFactory;

public abstract class HikariConfiguration<T extends HikariConnectionFactory> extends AbstractSqlConfiguration<T> {

    @Override
    @SneakyThrows
    public T factory() {
        T factory = this.factoryClass().newInstance();
        factory.init();
        this.setupFactory(factory);
        return factory;
    }

    public void setupFactory(T factory) {
        factory.configureDatabase(this.hostname(), this.port(), this.database(), this.username(), this.password());
        final HikariConfig config = factory.getConfig();
        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(10);
        config.setValidationTimeout(3000);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(60000);
    }
}
