package net.drapuria.framework.scheduler.pool;

import lombok.SneakyThrows;
import net.drapuria.framework.scheduler.helper.SchedulerHelper;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;
import net.drapuria.framework.task.ScheduledExecutorTask;
import net.drapuria.framework.task.TaskThread;

public class ScheduledExecutorSchedulerPool extends SchedulerPool<ScheduledExecutorTask>{
    public ScheduledExecutorSchedulerPool(long period, AbstractSchedulerProvider provider) {
        super(period, provider);
    }

    @SneakyThrows
    @Override
    public void start() {
        final long periodInMillis = SchedulerHelper.getDurationFromTicks(period, true);
        (super.task = new ScheduledExecutorTask()).start(periodInMillis, periodInMillis, this::handle);
    }
}
