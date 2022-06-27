/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.scheduler.provider;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.scheduler.pool.BukkitAsyncSchedulerPool;
import net.drapuria.framework.bukkit.impl.task.DrapuriaAsyncBukkitTask;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.provider.AsyncAbstractSchedulerProvider;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitAsyncSchedulerProvider extends AsyncAbstractSchedulerProvider {

    // TODO ADD SPIGOT 1.18 SERVER TO USE NMS

    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new DrapuriaAsyncBukkitTask()).start(0, 1, this::tickPool);
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

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new BukkitAsyncSchedulerPool(period, this));
    }

    @Override
    public void addOrCreatePool(Scheduler<?> scheduler) {
        new BukkitRunnable() {
            @Override
            public void run() {
                addSchedulerToPoolSync(scheduler);
            }
        }.runTask(Drapuria.PLUGIN);
    }

    private void addSchedulerToPoolSync(Scheduler<?> scheduler) {
        addToSuper(scheduler);
    }
}
