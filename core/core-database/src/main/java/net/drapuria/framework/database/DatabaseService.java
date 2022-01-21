/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

import net.drapuria.framework.beans.annotation.PreDestroy;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service(name = "FrameworkDatabaseService", dependencies = {"mongodb", "sql"})
public class DatabaseService {

    public static DatabaseService getDatabaseService;

    private final Map<String, HandlerGroup> handlerGroups = new HashMap<>();

    private ExecutorService executor;

    @PreInitialize
    public void preInit() {
        getDatabaseService = this;
        this.executor = Executors.newCachedThreadPool();
    }

    public void addGroup(final String name, final long executeDelay) {
        this.addGroup(name, executeDelay, null);
    }

    public void addGroup(final String name, final long executeDelay, final HandlerGroupType type) {
        if (this.handlerGroups.containsKey(name))
            return;
        this.handlerGroups.put(name, HandlerGroupFactory.factory()
                .name(name)
                .executeDelay(executeDelay)
                .groupType(type).build());
    }

    public boolean containsGroup(final String name) {
        return this.handlerGroups.containsKey(name);
    }

    public void removeGroup(final String name) {
        if (!this.handlerGroups.containsKey(name))
            return;
        final HandlerGroup handlerGroup = this.handlerGroups.get(name);
        handlerGroup.stopThead();
        this.handlerGroups.remove(name);
    }

    public void removeGroup(final HandlerGroup handlerGroup) {
        this.removeGroup(handlerGroup.getName());
    }

    public Optional<HandlerGroup> getHandlerGroup(final String name) {
        return Optional.ofNullable(this.handlerGroups.get(name));
    }

    @PreDestroy
    public void stopAll() {
        handlerGroups.values().forEach(HandlerGroup::stopThead);
        executor.shutdown();
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
