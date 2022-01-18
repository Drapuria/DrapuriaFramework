package net.drapuria.framework.scheduler.provider;

import net.drapuria.framework.scheduler.pool.ThreadedSchedulerPool;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import net.drapuria.framework.task.TaskThread;

public class ThreadedSchedulerProvider extends AbstractSchedulerProvider {

    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new TaskThread("SchedulerProvider")).start(0, 50, this::tickPool);
    }

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new ThreadedSchedulerPool(period, this));
    }
}
