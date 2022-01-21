/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler.provider;

import net.drapuria.framework.scheduler.pool.ThreadedSchedulerPool;
import net.drapuria.framework.task.ScheduledExecutorTask;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import net.drapuria.framework.task.TaskThread;

public class ScheduledExecutorSchedulerProvider extends AbstractSchedulerProvider {
    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new ScheduledExecutorTask()).start(0, 50, this::tickPool);
    }

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new ThreadedSchedulerPool(period, this));
    }
}
