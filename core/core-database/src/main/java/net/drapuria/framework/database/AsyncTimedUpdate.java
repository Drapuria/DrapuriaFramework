/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;


import net.drapuria.framework.FrameworkMisc;

public abstract class AsyncTimedUpdate implements DatabaseHandler {

    private boolean update;
    private boolean forceUpdate;
    private final long updateDelay;
    private String handlerName;
    private HandlerGroupType handlerGroupType;
    private HandlerGroup handlerGroup;

    public AsyncTimedUpdate(final String handlerName) {
        this(handlerName, 15_000L, true, null);
    }

    public AsyncTimedUpdate(final String handlerName, long updateDelay) {
        this(handlerName, updateDelay, true, null);
    }

    public AsyncTimedUpdate(final String handlerName, final long updateDelay, final boolean update) {
        this(handlerName, updateDelay, update, null);
    }

    public AsyncTimedUpdate(final String handlerName, long updateDelay, boolean update, HandlerGroupType handlerGroupType) {
        this.handlerName = handlerName;
        this.handlerGroupType = handlerGroupType;
        this.updateDelay = updateDelay;
        this.update = false;
        this.forceUpdate = false;
        if (update)
            registerHandler();
    }

    private void registerHandler() {
        final DatabaseService service = DatabaseService.getDatabaseService;
        if (service == null) {
            FrameworkMisc.TASK_SCHEDULER.runScheduled(this::registerHandler, 20L);
            return;
        }
        if (!service.containsGroup(handlerName))
            service.addGroup(handlerName, updateDelay, handlerGroupType);
        service.getHandlerGroup(handlerName).ifPresent(group -> {
            this.handlerGroup = group;
            this.handlerGroup.addHandler(this);
        });
    }

    public HandlerGroup getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(HandlerGroup handlerGroup) {
        this.handlerGroup.removeHandler(this);
        this.handlerGroup = handlerGroup;
        this.handlerGroup.addHandler(this);
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public long getUpdateDelay() {
        return updateDelay;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public HandlerGroupType getHandlerGroupType() {
        return handlerGroupType;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

}
