package net.drapuria.framework.bukkit.impl.scheduler.provider;

import net.drapuria.framework.bukkit.impl.scheduler.pool.BukkitAsyncSchedulerPool;
import net.drapuria.framework.bukkit.impl.task.DrapuriaAsyncBukkitTask;
import net.drapuria.framework.scheduler.provider.AsyncSchedulerProvider;
import net.drapuria.framework.task.TaskAlreadyStartedException;

public class BukkitAsyncSchedulerProvider extends AsyncSchedulerProvider {

    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new DrapuriaAsyncBukkitTask()).start(0, 1, this::tickPool);
    }

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new BukkitAsyncSchedulerPool(period));
    }
}
