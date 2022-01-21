/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.scheduler.pool;

import net.drapuria.framework.bukkit.impl.task.DrapuriaAsyncBukkitTask;
import lombok.SneakyThrows;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;


public class BukkitAsyncSchedulerPool extends BukkitSchedulerPool {

    public BukkitAsyncSchedulerPool(long period, AbstractSchedulerProvider provider) {
        super(period, provider);
    }

    @SneakyThrows
    @Override
    public void start() {
        (super.task = new DrapuriaAsyncBukkitTask()).start(super.period, super.period, this::handle);
    }

    @Override
    public void addScheduler(Scheduler<?> scheduler) {
        FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
           super.addScheduler(scheduler);
        });
    }

    @Override
    public void removeScheduler(Scheduler<?> scheduler) {
        FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
            super.removeScheduler(scheduler);
        });
    }

    @Override
    public void handle() {
        this.lastTickTime = System.currentTimeMillis();
        this.schedulers.removeIf(Scheduler::tick);
        if (this.schedulers.isEmpty()) {
            FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
                provider.removePool(this);
            });
        }
    }
}