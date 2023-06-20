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
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class SchedulerPool<T extends ITask> {

    protected final List<Scheduler<?>> schedulers = new ArrayList<>();
    protected final Queue<Scheduler<?>> toAdd = new LinkedBlockingQueue<>();
    protected final Queue<Scheduler<?>> toRemove = new LinkedBlockingQueue<>();
    protected final AbstractSchedulerProvider provider;
    @Getter
    protected long lastTickTime = System.currentTimeMillis();
    @Getter
    protected final long period;
    protected T task;

    private boolean scheduledRemove = false;

    public SchedulerPool(final long period, final AbstractSchedulerProvider provider) {
        this.period = period;
        this.provider = provider;
        start();
    }

    public abstract void start();

    public void handle() {
        if (this.scheduledRemove) {
            provider.removePool(this);
            return;
        }
        this.lastTickTime = System.currentTimeMillis();
        if (!toRemove.isEmpty()) {
            while (!toRemove.isEmpty())
                this.schedulers.remove(this.toRemove.poll());
        }
        if (!toAdd.isEmpty()) {
            while (!toAdd.isEmpty())
                this.schedulers.add(this.toAdd.poll());
        }
        for (Scheduler<?> scheduler : this.schedulers) {
            if (scheduler.tick())
                toRemove.add(scheduler);
        }
        if (this.schedulers.isEmpty())
            this.scheduledRemove = true;
    }

    @SneakyThrows
    public void shutdown() {
        task.shutdown();
        new ArrayList<>(this.schedulers).forEach(Scheduler::cancel);
        new ArrayList<>(this.toAdd).forEach(Scheduler::cancel);
        task = null;
    }

    public void addScheduler(Scheduler<?> scheduler) {
        if (scheduler == null)
            return;
        this.scheduledRemove = false;
        this.toAdd.add(scheduler);
        scheduler.setPool(this);
    }

    public void removeScheduler(Scheduler<?> scheduler) {
        this.schedulers.remove(scheduler);
    }

    public void scheduleSchedulerRemove(Scheduler<?> scheduler) {
        this.toRemove.add(scheduler);
    }
}