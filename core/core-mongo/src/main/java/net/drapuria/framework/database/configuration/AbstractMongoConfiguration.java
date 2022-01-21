/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.configuration;

import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;

public abstract class AbstractMongoConfiguration {

    public abstract String getDatabase();

    public MongoClientSettings buildMongoClientSettings() {
        MongoClientSettings.Builder builder = MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.JAVA_LEGACY);
        this.setupClientSettings(builder);
        return builder.build();
    }

    public boolean shouldActivate() {
        return true;
    }

    protected void setupClientSettings(MongoClientSettings.Builder builder) { }
}
