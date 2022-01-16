package net.drapuria.framework.bukkit.impl.scheduler.provider;

import net.drapuria.framework.bukkit.impl.scheduler.pool.BukkitSchedulerPool;
import net.drapuria.framework.bukkit.impl.task.DrapuriaBukkitTask;
import net.drapuria.framework.scheduler.provider.SchedulerProvider;
import net.drapuria.framework.task.TaskAlreadyStartedException;

public class BukkitSchedulerProvider extends SchedulerProvider {

    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new DrapuriaBukkitTask()).start(0, 1, this::tickPool);
    }

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new BukkitSchedulerPool(period));
    }
}