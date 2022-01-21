/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.configuration;

import net.drapuria.framework.database.connection.AbstractConnectionFactory;

public abstract class AbstractSqlConfiguration<F extends AbstractConnectionFactory> {

    public boolean shouldActivate() {
        return true;
    }


    public abstract Class<F> factoryClass();

    public abstract F factory();

    public abstract String hostname();

    public abstract String port();

    public abstract String database();

    public abstract String username();

    public abstract String password();

}
