package net.drapuria.framework.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTask implements ITask {

    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> scheduledFuture;;

    @Override
    public void shutdown() throws TaskNotStartedException {
        if (scheduledFuture == null)
            throw new TaskNotStartedException("Task not started");
        scheduledFuture.cancel(false);
        scheduledFuture = null;
    }

    @Override
    public void start(long delay, long period, Runnable runnable) throws TaskAlreadyStartedException {
        if (scheduledFuture != null)
            throw new TaskAlreadyStartedException("Task already started");
        this.scheduledFuture = service.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
