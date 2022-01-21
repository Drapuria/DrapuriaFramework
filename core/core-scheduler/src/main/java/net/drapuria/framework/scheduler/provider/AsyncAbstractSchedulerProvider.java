/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler.provider;

import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.scheduler.Scheduler;

public abstract class AsyncAbstractSchedulerProvider extends AbstractSchedulerProvider {


    @Override
    public void addOrCreatePool(Scheduler<?> scheduler) {
        FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
            super.addOrCreatePool(scheduler);
        });
    }

    public void addToSuper(Scheduler<?> scheduler) {
        super.addOrCreatePool(scheduler);
    }

}
