/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler.pool;

import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;
import net.drapuria.framework.task.ITask;

import java.util.ArrayList;
import java.util.List;

public abstract class SchedulerPool<T extends ITask> {

    protected final List<Scheduler<?>> schedulers = new ArrayList<>();
    protected final AbstractSchedulerProvider provider;
    @Getter
    protected long lastTickTime = System.currentTimeMillis();
    @Getter
    protected final long period;
    protected T task;

    public SchedulerPool(final long period, final AbstractSchedulerProvider provider) {
        this.period = period;
        this.provider = provider;
        start();
    }

    public abstract void start();

    public void handle() {
        this.lastTickTime = System.currentTimeMillis();
        this.schedulers.removeIf(Scheduler::tick);
        if (this.schedulers.isEmpty())
            provider.removePool(this);
    }

    @SneakyThrows
    public void shutdown() {
        task.shutdown();
        new ArrayList<>(this.schedulers).forEach(Scheduler::cancel);
    }

    public void addScheduler(Scheduler<?> scheduler) {
        this.schedulers.add(scheduler);
        scheduler.setPool(this);
    }

    public void removeScheduler(Scheduler<?> scheduler) {
        this.schedulers.remove(scheduler);
    }
}