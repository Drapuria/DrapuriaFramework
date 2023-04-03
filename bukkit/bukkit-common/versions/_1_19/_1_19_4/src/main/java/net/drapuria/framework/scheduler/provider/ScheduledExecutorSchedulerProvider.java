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

    @Override
    public void tickPool() {
        scheduledSchedulers.entrySet().removeIf(entry -> {
            long delay = entry.getValue();
            if (--delay <= 0) {
                entry.getKey().tick();
                addOrCreatePool(entry.getKey());
                return true;
            }
            scheduledSchedulers.put(entry.getKey(), delay);  // java 17 fix
            return false;
        });
    }
}
