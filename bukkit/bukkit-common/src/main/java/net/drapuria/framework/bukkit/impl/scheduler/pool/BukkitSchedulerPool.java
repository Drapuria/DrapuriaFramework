package net.drapuria.framework.bukkit.impl.scheduler.pool;

import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.impl.task.DrapuriaBukkitTask;
import net.drapuria.framework.scheduler.pool.SchedulerPool;

public class BukkitSchedulerPool extends SchedulerPool<DrapuriaBukkitTask> {
    public BukkitSchedulerPool(long period) {
        super(period);
    }

    @SneakyThrows
    @Override
    public void start() {
        (super.task = new DrapuriaBukkitTask()).start(super.period, super.period, this::handle);
    }
}
