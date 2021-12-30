package net.drapuria.framework.database;

import net.drapuria.framework.services.ComponentRegistry;
import net.drapuria.framework.services.PostDestroy;
import net.drapuria.framework.services.PreInitialize;
import net.drapuria.framework.services.Service;

import java.util.HashMap;
import java.util.Map;

@Service(name = "mongodb", dependencies = "serializer")
public class MongoService {

    private Class<?> defaultConfiguration;
    private final Map<Class<?>, MongoDbFactory> databases = new HashMap<>();

    @PreInitialize
    public void preInit() {
        ComponentRegistry.registerComponentHolder(new MongoComponentHolder(this));
    }

    @PostDestroy
    public void stop() {
        databases.keySet().forEach(this::shutdownDatabase);
    }

    public Class<?> getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public void setDefaultConfiguration(Class<?> defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public void addDatabase(Class<?> configuration, MongoDbFactory factory) {
        databases.put(configuration, factory);
    }

    public void shutdownDatabase(Class<?> configuration) {
        final MongoDbFactory mongoDbFactory = this.databases.get(configuration);

        if (mongoDbFactory != null) {
            mongoDbFactory.getClient().close();
        }
        if (configuration == this.defaultConfiguration)
            this.defaultConfiguration = null;
        this.databases.remove(configuration);
    }
}
