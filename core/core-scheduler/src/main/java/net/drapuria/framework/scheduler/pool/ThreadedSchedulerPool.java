package net.drapuria.framework.scheduler.pool;

import lombok.SneakyThrows;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;
import net.drapuria.framework.task.TaskThread;
import net.drapuria.framework.scheduler.helper.SchedulerHelper;

public class ThreadedSchedulerPool extends SchedulerPool<TaskThread> {

    private static ThreadGroup threadGroup;

    public ThreadedSchedulerPool(long period, AbstractSchedulerProvider provider) {
        super(period, provider);
        if (threadGroup == null) {
            threadGroup = new ThreadGroup("ThreadedSchedulerPool");
        }
    }

    @SneakyThrows
    @Override
    public void start() {
        final long periodInMillis = SchedulerHelper.getDurationFromTicks(period, true);
        System.out.println("ticks: " + period + " millis: " + periodInMillis);
        (super.task = new TaskThread(threadGroup, "ThreadedSchedulerPool-" + this.period))
                .start(periodInMillis, periodInMillis, this::handle);
    }
}
