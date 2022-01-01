package net.drapuria.framework.database;

import net.drapuria.framework.database.component.SqlConfigurationComponentHolder;
import net.drapuria.framework.database.configuration.AbstractSqlConfiguration;
import net.drapuria.framework.database.connection.AbstractConnectionFactory;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.libraries.Library;
import net.drapuria.framework.services.*;

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

    private AbstractSqlConfiguration<?> defaultConfiguration = null;

    @Autowired
    private SerializerFactory serializerFactory;

    @PreInitialize
    private void preInit() {
        getService = this;

        FrameworkMisc.LIBRARY_HANDLER.downloadLibraries(true, Library.BYTE_BUDDY);
        ComponentRegistry.registerComponentHolder(new SqlConfigurationComponentHolder(this));
    }

    public AbstractSqlConfiguration<?> getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public void setDefaultConfiguration(AbstractSqlConfiguration<?> defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public void addConnectionFactory(AbstractSqlConfiguration<?> configuration, AbstractConnectionFactory factory) {
        connectionFactories.put(configuration.getClass(), factory);
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
            conn = getConnectionFactories().get(defaultConfiguration.getClass()).getConnection();
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

}
