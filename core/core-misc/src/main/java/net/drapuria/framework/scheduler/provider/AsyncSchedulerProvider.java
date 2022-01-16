package net.drapuria.framework.scheduler.provider;

import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.scheduler.Scheduler;

public abstract class AsyncSchedulerProvider extends SchedulerProvider {


    @Override
    protected void addOrCreatePool(Scheduler<?> scheduler) {
        FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
            super.addOrCreatePool(scheduler);
        });
    }

}
