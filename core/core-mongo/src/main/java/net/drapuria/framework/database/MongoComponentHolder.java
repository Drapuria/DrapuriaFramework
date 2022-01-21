/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import net.drapuria.framework.database.configuration.AbstractMongoConfiguration;
import net.drapuria.framework.beans.component.ComponentHolder;

public class MongoComponentHolder extends ComponentHolder {

    private final MongoService mongoService;

    public MongoComponentHolder(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public Class<?>[] type() {
        return new Class[] {AbstractMongoConfiguration.class};
    }

    @Override
    public void onEnable(Object instance) {
        AbstractMongoConfiguration configuration = (AbstractMongoConfiguration) instance;
        if (!configuration.shouldActivate())
            return;
        if (mongoService.getDefaultConfiguration() == null) {
            mongoService.setDefaultConfiguration(configuration.getClass());
        }
        MongoClientSettings mongoClientSettings = configuration.buildMongoClientSettings();
        final MongoClient client = MongoClients.create(mongoClientSettings);
        this.mongoService.addDatabase(configuration.getClass(), new MongoDbFactory()
                .client(client)
                .database(client.getDatabase(configuration.getDatabase())));
    }

    @Override
    public void onDisable(Object instance) {
        this.mongoService.shutdownDatabase(instance.getClass());
    }
}
