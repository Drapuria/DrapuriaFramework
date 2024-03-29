/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

public class ThreadedHandlerGroup extends HandlerGroup {

    private Thread thread;

    public ThreadedHandlerGroup(String name, long executeDelay) {
        super(name, executeDelay);
    }

    @Override
    public void startGroup() {
        if (this.thread != null && this.thread.isAlive())
            return;
        super.active = true;
        this.thread = new Thread(() -> {
            try {
                Thread.sleep(super.executeDelay);
            } catch (InterruptedException ignored) {

            }
            if (getHandlerList() != null)
                executeAll();
        }, "HandlerGroup Thread - " + this.name);

        this.thread.start();
    }

    @Override
    public boolean isRunning() {
        return this.thread != null && this.thread.isAlive();
    }

    @Override
    public void stopThead() {
        if (this.thread == null || !this.thread.isAlive())
            return;
        super.active = false;
        this.thread.interrupt();
        this.thread = null;
        executeAll();
    }
}
