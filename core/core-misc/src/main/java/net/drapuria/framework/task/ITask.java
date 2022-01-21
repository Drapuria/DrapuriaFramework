/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.task;

/**
 * Interface to start Tasks
 */
public interface ITask {

    /**
     * Shuts the task down
     *
     * @throws TaskNotStartedException Will get thrown if task is not started
     */
    void shutdown() throws TaskNotStartedException;

    /**
     *
     * Start the {@link ITask}
     *
     * @param delay The task delay
     * @param period The task period
     * @param runnable The runnable
     * @throws TaskAlreadyStartedException Will get thrown if task is already started
     */
    void start(long delay, long period, Runnable runnable) throws TaskAlreadyStartedException;

    /*
     * @return Does the thread run on the Main Thread
     */
    boolean isAsync();

}
