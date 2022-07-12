/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.scheduler.provider;

import net.drapuria.framework.bukkit.impl.scheduler.pool.BukkitSchedulerPool;
import net.drapuria.framework.bukkit.impl.task.DrapuriaBukkitTask;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;
import net.drapuria.framework.task.TaskAlreadyStartedException;

public class BukkitSchedulerProvider extends AbstractSchedulerProvider {

    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new DrapuriaBukkitTask()).start(0, 1, this::tickPool);
    }

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new BukkitSchedulerPool(period, this));
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