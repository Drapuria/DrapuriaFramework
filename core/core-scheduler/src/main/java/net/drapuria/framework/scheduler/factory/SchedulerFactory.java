/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler.factory;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.SchedulerService;
import net.drapuria.framework.scheduler.TickTime;
import net.drapuria.framework.scheduler.Timestamp;
import net.drapuria.framework.scheduler.action.ActionPriority;
import net.drapuria.framework.scheduler.action.RepeatedAction;
import net.drapuria.framework.scheduler.action.ScheduledAction;
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

/**
 * Uses chained setters to create {@link Scheduler}
 *
 *
 * @param <T> The Type that is used in the {@link Supplier}
 */
@Accessors(fluent = true, chain = true)
@Setter
public class SchedulerFactory<T> {

    @Setter(NONE)
    private final List<RepeatedAction<T>> repeatedActions = new ArrayList<>();
    @Setter(NONE)
    private final Map<Long, ScheduledAction<T>> scheduledActions = new HashMap<>();
    @Setter(NONE)
    private final Map<Timestamp, ScheduledAction<T>> timedEvents = new HashMap<>();

    /**
     * The initial delay of the scheduler in {@link TickTime} (1 Tick equals 50ms)
     */
    private long delay;


    /**
     * The period of the scheduler in {@link TickTime} (1 Tick equals 50ms)
     */
    private long period;

    /**
     * The amount of iterations the {@link Scheduler} should run.
     *
     *
     * If set to {@code -1} the {@link Scheduler} will run forever.
     */
    private long iterations = -2;

    /**
     * The object supplied to the {@link Scheduler}
     */
    private Supplier<T> supplier;

    /**
     * The iterations between updates of the supplied content
     */
    private long supplierUpdateIterations = -1;

    /**
     * The {@link AbstractSchedulerProvider} of the {@link Scheduler}
     */
    private Class<? extends AbstractSchedulerProvider> provider = ThreadedSchedulerProvider.class;

    /**
     * Sets the wrapped delay of the scheduler while converting it to ticks
     *
     * @param delay the delay in units
     * @param tickTime The TickTime
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> delay(long delay, TickTime tickTime) {
        return delay(tickTime.getTicks() * delay);
    }


    /**
     * Sets the wrapped delay of the scheduler while converting it to ticks
     *
     * @param period the delay in units
     * @param tickTime The TickTime
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> period(long period, TickTime tickTime) {
        return period(tickTime.getTicks() * period);
    }


    /**
     * Adds a {@link RepeatedAction} for {@link Scheduler}
     *
     * @param repeatedAction repeatedly performed action
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> repeated(RepeatedAction<T> repeatedAction) {
        this.repeatedActions.add(repeatedAction);
        return this;
    }

    /**
     * Sets the tick delay between supplier updates for {@link Scheduler}
     *
     * @param iterations iteration delay between supplier updates
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> supplierUpdateIterations(long iterations) {
        this.supplierUpdateIterations = iterations;
        return this;
    }

    /**
     * Adds an always performing {@link RepeatedAction} for {@link Scheduler}
     *
     * @param repeatedAction repeatedly performed action
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> repeated(BiConsumer<Long, T> repeatedAction) {
        return repeated(new RepeatedAction<>(true, true, true, 0, 0, repeatedAction));
    }

    /**
     * Adds a {@link ScheduledAction} for {@link Scheduler}
     *
     * @param timestamp The time at which the action should be executed
     * @param action The performed action
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> at(Timestamp timestamp, Consumer<T> action) {
        return at(timestamp, new ScheduledAction<>(action, ActionPriority.NORMAL));
    }

    /**
     * Adds a {@link ScheduledAction} for {@link Scheduler}
     *
     * @param timestamp The time at which the action should be executed
     * @param action The performed action
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> at(Timestamp timestamp, ScheduledAction<T> action) {
        if (this.iterations == -2) {
            this.timedEvents.put(timestamp, action);
            return this;
        } else return at(SchedulerHelper.convertTimestampToIteration(timestamp, iterations), action);
    }

    /**
     * Adds a {@link ScheduledAction} for {@link Scheduler}
     *
     * @param iteration The time at which the action should be exected
     * @param action The performed action
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> at(long iteration, Consumer<T> action) {
        return at(iteration, new ScheduledAction<>(action, ActionPriority.NORMAL));
    }

    /**
     * Adds a {@link ScheduledAction} for {@link Scheduler}
     *
     * @param iteration The time at which the action should be exected
     * @param action The performed action
     * @return {@link SchedulerFactory}
     */
    public SchedulerFactory<T> at(long iteration, ScheduledAction<T> action) {
        this.scheduledActions.put(iteration, action);
        return this;
    }

    /**
     * Converts the {@link Timestamp} actions to a {@link Long}
     */
    private void buildInternal() {
        this.timedEvents.forEach((timestamp, consumer) -> {
            this.scheduledActions.put(SchedulerHelper.convertTimestampToIteration(timestamp, this.iterations), consumer);
        });
    }

    /**
     * Builds the {@link Scheduler}
     *
     * @return {@link Scheduler}
     */
    public Scheduler<?> build() {
        buildInternal();
        Scheduler<T> scheduler = new Scheduler<>(delay, period, iterations);
        scheduler.getTimedActions().putAll(scheduledActions);
        scheduler.getRepeatedActions().addAll(repeatedActions);
        scheduler.setSupplier(supplier);
        scheduler.setSupplierUpdateIterations(supplierUpdateIterations);
        final AbstractSchedulerProvider provider = SchedulerService.getService.getProvider(this.provider);
        provider.addSchedulerToProvider(scheduler);
        scheduler.setProvider(provider);
        return scheduler;
    }
}