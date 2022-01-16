package net.drapuria.framework.bukkit.impl.scheduler;

import net.drapuria.framework.scheduler.Scheduler;

public class DrapuriaScheduler<T> extends Scheduler<T> {

    public DrapuriaScheduler(long delay) {
        super(delay);
    }

    public DrapuriaScheduler(long delay, long period) {
        super(delay, period);
    }

    public DrapuriaScheduler(long delay, long period, long iterations) {
        super(delay, period, iterations);
    }
}
