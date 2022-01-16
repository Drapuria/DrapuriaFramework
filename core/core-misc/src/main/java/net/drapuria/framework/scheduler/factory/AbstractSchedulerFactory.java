package net.drapuria.framework.scheduler.factory;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.TickTime;
import net.drapuria.framework.scheduler.Timestamp;
import net.drapuria.framework.scheduler.action.RepeatedAction;
import net.drapuria.framework.scheduler.helper.SchedulerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Accessors(fluent = true, chain = true)
@Setter
public abstract class AbstractSchedulerFactory<T, S extends Scheduler<T>> {

    protected final List<RepeatedAction<T>> repeatedActions = new ArrayList<>();
    protected final Map<Long, Consumer<T>> scheduledEvents = new HashMap<>();
    private final Map<Timestamp, Consumer<T>> timedEvents = new HashMap<>();
    protected long delay;
    protected long period;
    protected long iterations = -2;
    protected Supplier<T> supplier;

    public AbstractSchedulerFactory<T, S> delay(long delay, TickTime tickTime) {
        return delay(tickTime.getTicks() * delay);
    }

    public AbstractSchedulerFactory<T, S> period(long period, TickTime tickTime) {
        return period(tickTime.getTicks() * period);
    }

    public AbstractSchedulerFactory<T, S> repeated(RepeatedAction<T> repeatedAction) {
        this.repeatedActions.add(repeatedAction);
        return this;
    }

    public AbstractSchedulerFactory<T, S> at(Timestamp timestamp, Consumer<T> event) {
        if (this.iterations == -2) {
            this.timedEvents.put(timestamp, event);
            return this;
        } else return at(SchedulerHelper.convertTimestampToIteration(timestamp, iterations), event);
    }

    public AbstractSchedulerFactory<T, S> at(long iteration, Consumer<T> event) {
        this.scheduledEvents.put(iteration, event);
        return this;
    }

    protected void buildInternal() {
        this.timedEvents.forEach((timestamp, consumer) -> {
            this.scheduledEvents.put(SchedulerHelper.convertTimestampToIteration(timestamp, this.iterations), consumer);
        });
    }
    public abstract S build();
}
