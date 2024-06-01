/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

import java.util.List;

public abstract class TimedDatabaseUpdate extends AsyncTimedUpdate implements CompletionState {

    private boolean timedUpdate;
    private CompletionState completionState;

    public TimedDatabaseUpdate(final String handlerName, final boolean timedUpdate) {
        this(handlerName, timedUpdate, 10000L);
    }


    public TimedDatabaseUpdate(final String handlerName, final boolean timedUpdate, final long executeDelay) {
        super(handlerName, executeDelay, timedUpdate);
        this.completionState = new CompletionStateImpl();
        this.timedUpdate = timedUpdate;
    }

    public void setTimedUpdate(boolean timedUpdate) {
        if (!this.timedUpdate && timedUpdate) {
            if (!DatabaseService.getDatabaseService.containsGroup(getHandlerName()))
                DatabaseService.getDatabaseService.addGroup(getHandlerName(), getUpdateDelay());
            DatabaseService.getDatabaseService.getHandlerGroup(getHandlerName()).ifPresent(this::setHandlerGroup);
            getHandlerGroup().addHandler(this);
        }
        if (this.timedUpdate && !timedUpdate &&
                getHandlerGroup().getHandlerList().contains(this)) {
            getHandlerGroup().removeHandler(this);
        }
        this.timedUpdate = timedUpdate;
    }

    public boolean isTimedUpdate() {
        return this.timedUpdate;
    }

    public void setUpdate(boolean state) {
        if (state && !this.timedUpdate) {
            saveData();
            return;
        }
        super.setUpdate(state);
    }

    public void execute() {
        if (!isUpdate() && !isForceUpdate())
            return;
        saveData();
        setUpdate(false);
    }

    public void saveDataAsync() {
        DatabaseService.getDatabaseService.getExecutor().execute(this::saveData);
    }


    public void loadDataAsync() {
        DatabaseService.getDatabaseService.getExecutor().execute(this::loadData);
    }


    public void deleteDataAsync() {
        DatabaseService.getDatabaseService.getExecutor().execute(this::deleteData);
    }

    public void addReadyExecutor(Runnable exec) {
        this.completionState.addReadyExecutor(exec);
    }


    public List<Runnable> getReadyExecutors() {
        return this.completionState.getReadyExecutors();
    }


    public boolean isReady() {
        return this.completionState.isReady();
    }


    public void setReady(boolean ready) {
        this.completionState.setReady(ready);
    }

    public abstract void saveData();

    public abstract void loadData();

    public abstract void deleteData();
}
