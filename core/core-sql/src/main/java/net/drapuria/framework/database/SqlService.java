/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

import net.drapuria.framework.ProvideConfiguration;
import net.drapuria.framework.RepositoryType;
import net.drapuria.framework.database.component.SqlConfigurationComponentHolder;
import net.drapuria.framework.database.configuration.AbstractSqlConfiguration;
import net.drapuria.framework.database.connection.AbstractConnectionFactory;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.jackson.libraries.Library;
import net.drapuria.framework.beans.*;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentRegistry;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service(name = "sql", dependencies = "serializer")
public class SqlService {

    public static SqlService getService;

    private final Map<Class<?>, AbstractConnectionFactory> connectionFactories = new HashMap<>();

    private Class<?> defaultConfiguration = null;

    @Autowired
    private SerializerFactory serializerFactory;

    @PreInitialize
    private void preInit() {
        getService = this;

        FrameworkMisc.LIBRARY_HANDLER.downloadLibraries(true, Library.BYTE_BUDDY);
        ComponentRegistry.registerComponentHolder(new SqlConfigurationComponentHolder(this));
    }

    public Class<?> getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public void setDefaultConfiguration(Class<?> defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public void addConnectionFactory(Class<?> configuration, AbstractConnectionFactory factory) {
        connectionFactories.put(configuration, factory);
    }

    public Map<Class<?>, AbstractConnectionFactory> getConnectionFactories() {
        return connectionFactories;
    }

    public ObjectSerializer<?, ?> findSerializer(Class<?> type) {
        return this.serializerFactory.findSerializer(type);
    }

    public void shutdownFactory(Class<?> configuration) {
        AbstractConnectionFactory connectionFactory = this.connectionFactories.get(configuration);
        if (connectionFactory != null) {
            try {
                connectionFactory.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.connectionFactories.remove(configuration);
        }
    }

    public void executeUpdate(String statement) {
        Connection conn = null;
        try {
            conn = getConnectionFactories().get(defaultConfiguration).getConnection();
            PreparedStatement st = conn.prepareStatement(statement);
            st.executeUpdate();
            st.close();
        } catch (Exception e) {
            FrameworkMisc.PLATFORM.getLogger().warn("Could not perform executeUpdate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(conn != null && !(conn.isClosed()))
                    conn.close();
            } catch(SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void executeUpdate(PreparedStatement statement) {
        try {
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            FrameworkMisc.PLATFORM.getLogger().warn("Could not perform executeUpdate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (Exception e) {
            FrameworkMisc.PLATFORM.getLogger().warn("Could not perform executeQuery: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public AbstractConnectionFactory factory(Class<?> use, @Nullable RepositoryType repositoryType) {
        Class<?> type;
        ProvideConfiguration configuration = use.getAnnotation(ProvideConfiguration.class);
        if (configuration != null) {
            type = configuration.value();
        } else {
            if (repositoryType != null) {
                for (AbstractConnectionFactory factory : this.connectionFactories.values()) {
                    if (factory.type() == repositoryType) {
                        return factory;
                    }
                }

                throw new IllegalArgumentException("There is no sql configuration with specified type " + repositoryType.name() + " registered!");
            }

            type = this.defaultConfiguration;

            if (type == null) {
                throw new IllegalArgumentException("There is no sql configuration registered!");
            }
        }

        if (!AbstractSqlConfiguration.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("The type " + type.getSimpleName() + " wasn't implemented on AbstractMongoConfiguration!");
        }

        if (this.connectionFactories == null) {
            throw new IllegalArgumentException("SQLService haven't been loaded!");
        }

        AbstractConnectionFactory factory = this.connectionFactories.getOrDefault(type, null);
        if (factory == null) {
            throw new IllegalArgumentException("The database hasn't registered!");
        }

        return factory;
    }

}
