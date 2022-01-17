package net.drapuria.framework.scheduler.factory;

import lombok.experimental.Accessors;
import net.drapuria.framework.scheduler.Scheduler;

@Accessors(chain = true, fluent = true)
public abstract class AbstractAsyncSchedulerFactory<T, S extends Scheduler<T>> extends AbstractSchedulerFactory<T, S>{

    protected boolean async = true;

}
