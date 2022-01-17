package net.drapuria.framework.bukkit.impl.scheduler.pool;

import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.impl.task.DrapuriaBukkitTask;
import net.drapuria.framework.scheduler.pool.SchedulerPool;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;

public class BukkitSchedulerPool extends SchedulerPool<DrapuriaBukkitTask> {
    public BukkitSchedulerPool(long period, AbstractSchedulerProvider provider) {
        super(period, provider);
    }

    @SneakyThrows
    @Override
    public void start() {
        (super.task = new DrapuriaBukkitTask()).start(super.period, super.period, this::handle);
    }
}
