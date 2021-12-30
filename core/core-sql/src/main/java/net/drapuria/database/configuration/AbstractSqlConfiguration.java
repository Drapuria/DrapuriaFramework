package net.drapuria.database.configuration;

import net.drapuria.database.connection.AbstractConnectionFactory;

public abstract class AbstractSqlConfiguration<F extends AbstractConnectionFactory> {

    public abstract Class<F> factoryClass();

    public abstract F factory();

    public boolean shouldActivate() {
        return true;
    }

}
