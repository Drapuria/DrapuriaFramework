/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;


import java.util.concurrent.CopyOnWriteArrayList;

public abstract class HandlerGroup {

    protected final String name;
    protected final long executeDelay;
    private final CopyOnWriteArrayList<DatabaseHandler> handlerList = new CopyOnWriteArrayList<>();
    protected boolean active;

    public HandlerGroup(final String name, long executeDelay) {
        this.name = name;
        this.executeDelay = executeDelay;
        startGroup();
    }

    public void addHandler(DatabaseHandler handler) {
        if (this.handlerList.contains(handler))
            return;
        this.handlerList.add(handler);
        if (!isRunning())
            startGroup();
    }

    public void removeHandler(DatabaseHandler handler) {
        this.handlerList.remove(handler);
    }

    protected void executeAll() {
        this.handlerList.forEach(DatabaseHandler::execute);
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public CopyOnWriteArrayList<DatabaseHandler> getHandlerList() {
        return handlerList;
    }


    public abstract void startGroup();

    public abstract boolean isRunning();

    public abstract void stopThead();

}
