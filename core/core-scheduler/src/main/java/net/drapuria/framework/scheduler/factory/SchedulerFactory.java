package net.drapuria.framework.scheduler.factory;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.SchedulerService;
import net.drapuria.framework.scheduler.TickTime;
import net.drapuria.framework.scheduler.Timestamp;
import net.drapuria.framework.scheduler.action.RepeatedAction;
import net.drapuria.framework.scheduler.helper.SchedulerHelper;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;
import net.drapuria.framework.scheduler.provider.ThreadedSchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static lombok.AccessLevel.NONE;

@Accessors(fluent = true, chain = true)
@Setter
public class SchedulerFactory<T> {

    @Setter(NONE)
    private final List<RepeatedAction<T>> repeatedActions = new ArrayList<>();
    @Setter(NONE)
    private final Map<Long, Consumer<T>> scheduledEvents = new HashMap<>();
    @Setter(NONE)
    private final Map<Timestamp, Consumer<T>> timedEvents = new HashMap<>();
    private long delay;
    private long period;
    private long iterations = -2;
    private Supplier<T> supplier;
    private Class<? extends AbstractSchedulerProvider> provider = ThreadedSchedulerProvider.class;

    public SchedulerFactory<T> delay(long delay, TickTime tickTime) {
        return delay(tickTime.getTicks() * delay);
    }

    public SchedulerFactory<T> period(long period, TickTime tickTime) {
        return period(tickTime.getTicks() * period);
    }

    public SchedulerFactory<T> repeated(RepeatedAction<T> repeatedAction) {
        this.repeatedActions.add(repeatedAction);
        return this;
    }

    public SchedulerFactory<T> repeated(BiConsumer<Long, T> repeatedAction) {
        return repeated(new RepeatedAction<>(true, true, true, 0, 0, repeatedAction));
    }

    public SchedulerFactory<T> at(Timestamp timestamp, Consumer<T> event) {
        if (this.iterations == -2) {
            this.timedEvents.put(timestamp, event);
            return this;
        } else return at(SchedulerHelper.convertTimestampToIteration(timestamp, iterations), event);
    }

    public SchedulerFactory<T> at(long iteration, Consumer<T> event) {
        this.scheduledEvents.put(iteration, event);
        return this;
    }

    private void buildInternal() {
        this.timedEvents.forEach((timestamp, consumer) -> {
            this.scheduledEvents.put(SchedulerHelper.convertTimestampToIteration(timestamp, this.iterations), consumer);
        });
    }

    public Scheduler<?> build() {
        buildInternal();
        Scheduler<T> scheduler = new Scheduler<>(delay, period, iterations);
        scheduler.getTimedActions().putAll(scheduledEvents);
        scheduler.getRepeatedActions().addAll(repeatedActions);
        scheduler.setSupplier(supplier);
        final AbstractSchedulerProvider provider = SchedulerService.getService.getProvider(this.provider);
        provider.addSchedulerToPool(scheduler);
        return scheduler;
    }
}