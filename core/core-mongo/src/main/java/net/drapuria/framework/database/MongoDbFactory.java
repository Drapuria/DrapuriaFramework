/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

@Getter
public class MongoDbFactory {

    private MongoClient client;
    private MongoDatabase database;

    public static MongoDbFactory factory() {
        return new MongoDbFactory();
    }

    public MongoDbFactory client(final MongoClient client) {
        this.client = client;
        return this;
    }

    public MongoDbFactory database(final MongoDatabase database) {
        this.database = database;
        return this;
    }

    public boolean isReady() {
        return this.client != null && this.database != null;
    }
}
