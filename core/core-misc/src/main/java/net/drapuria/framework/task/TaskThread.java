/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.task;

import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.IThread;
import net.drapuria.framework.task.ITask;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import net.drapuria.framework.task.TaskNotStartedException;

/**
 * Represents a {@link ITask}
 */
public class TaskThread extends Thread implements IThread, ITask {

    private long delay;
    private long period;
    private Runnable tickRunnable;
    private boolean stopped;

    /**
     *
     * @param name Task name
     */
    public TaskThread(String name) {
        super(name);
    }

    /**
     *
     * @param threadGroup The group the task runs in
     * @param name The name of the task
     */
    public TaskThread(ThreadGroup threadGroup, String name) {
        super(threadGroup, name);
    }

    @Override
    public void shutdown() throws TaskNotStartedException {
        if (stopped)
            throw new TaskNotStartedException("Task not running");
        if (FrameworkMisc.PLATFORM.isShuttingDown()) {
           try {
               interrupt();
           } catch (Exception ignored) {

           }
        }
        stopped = true;
    }

    @Override
    public void start(long delay, long period, Runnable runnable) throws TaskAlreadyStartedException {
        if (isAlive() && !interrupted())
            throw new TaskAlreadyStartedException("Task already started.");
        this.delay = delay;
        this.period = period;
        this.tickRunnable = runnable;
        start();
    }

    /**
     *
     * A Thread is always not synchronized with the main thread.
     *
     * @return true
     */
    @Override
    public boolean isAsync() {
        return true;
    }

    /**
     * This could be done cleaner, but yeah
     */
    @Override
    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }
        if (stopped)
            return;
        this.tickRunnable.run();
        while (!stopped) {
            try {
                Thread.sleep(period);
            } catch (InterruptedException ignored) {

            }
            if (stopped)
                return;
            this.tickRunnable.run();
        }
    }
}