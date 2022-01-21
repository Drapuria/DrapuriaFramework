/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SingleThreadedHandlerGroup extends HandlerGroup {

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> scheduledFuture;

    public SingleThreadedHandlerGroup(String name, long executeDelay) {
        super(name, executeDelay);
    }

    @Override
    public void startGroup() {
        scheduledFuture = executorService.scheduleWithFixedDelay(
                this::executeAll,
                executeDelay,
                executeDelay,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isRunning() {
        return scheduledFuture != null && !scheduledFuture.isCancelled();
    }

    @Override
    public void stopThead() {
        if (scheduledFuture == null || scheduledFuture.isCancelled())
            return;
        super.active = false;
        scheduledFuture.cancel(false);
        scheduledFuture = null;
        executeAll();
    }
}
