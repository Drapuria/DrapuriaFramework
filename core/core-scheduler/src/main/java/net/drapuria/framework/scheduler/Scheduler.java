package net.drapuria.framework.scheduler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.scheduler.action.ActionPriority;
import net.drapuria.framework.scheduler.action.RepeatedAction;
import net.drapuria.framework.scheduler.action.ScheduledAction;
import net.drapuria.framework.scheduler.helper.SchedulerHelper;
import net.drapuria.framework.scheduler.pool.SchedulerPool;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @param <T> The scheduled object
 */
@Getter
public class Scheduler<T> {

    protected final Map<Long, ScheduledAction<T>> timedActions = new HashMap<>();
    protected final List<RepeatedAction<T>> repeatedActions = new ArrayList<>();

    private AbstractSchedulerProvider provider;
    private SchedulerPool<?> pool;

    @Setter
    private long delay;
    private long period;
    protected long iterations;

    private long currentIteration;

    private long startTime = System.currentTimeMillis();
    private long duration;
    private long endTime;

    @Setter(AccessLevel.PUBLIC)
    private Supplier<T> supplier;

    public Scheduler(long delay) {
        this(delay, 0, 0);
    }

    public Scheduler(long delay, long period) {
        this(delay, period, -1);
    }

    private boolean cancelled = false;

    public Scheduler(long delay, long period, long iterations) {
        this.delay = delay;
        this.period = period;
        this.iterations = iterations;
        this.endTime = this.startTime + (this.duration = SchedulerHelper.getDurationFromTicks(iterations * period, true));
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean tick() {
        if (cancelled)
            return true;
        if (iterations != -1)
            iterations--;
        final long expiredTime = System.currentTimeMillis() - getStartTime();
        currentIteration++;
        try {
            repeatedActions.forEach(repeatedAction -> {
                if (repeatedAction.isAlways()) {
                    if (!repeatedAction.isLastTick() && iterations == 0) return;
                    if (!repeatedAction.isFirstTick() && currentIteration == 1) return;
                    repeatedAction.getAction().accept(expiredTime, getSupplier().get());
                } else if (iterations % repeatedAction.getDivision() == repeatedAction.getRemainder())
                    repeatedAction.getAction().accept(expiredTime, getSupplier().get());
            });
            ScheduledAction<T> scheduledAction = parseAction(currentIteration);
            if (scheduledAction != null)
                scheduledAction.accept(supplier.get());
        } catch (Exception ignored) {
        }

        return iterations == 0;
    }

    public ScheduledAction<T> parseAction(long time) {
        return timedActions.get(time);
    }

    public ScheduledAction<T> parseAction(Timestamp timestamp) {
        return parseAction(SchedulerHelper.convertTimestampToIteration(timestamp, this.iterations));
    }

    public void setProvider(AbstractSchedulerProvider provider) {
        if (this.provider != null)
            return;
        this.provider = provider;
    }

    public void setPool(SchedulerPool<?> pool) {
        if (this.pool != null)
            return;
        this.pool = pool;
    }

    /**
     * Cancels the {@link Scheduler<T>}
     */
    public void cancel() {
        if (cancelled)
            return;
        this.cancelled = true;
        final long currentTime = iterations;

        this.timedActions.forEach((key, scheduledAction) -> {
            if (scheduledAction.getPriority() == ActionPriority.HIGH && (iterations == -1 || currentIteration < key)) {
                try {
                    scheduledAction.accept(supplier.get());
                } catch (Exception ignored) {
                }
            }
        });

        this.repeatedActions.forEach(scheduledAction -> {
            if (scheduledAction.getPriority() == ActionPriority.HIGH) {
                try {
                    scheduledAction.getAction().accept(-1L, supplier.get());
                } catch (Exception ignored) {
                }
            }
        });
        if (pool != null) {
            pool.removeScheduler(this);
        }
    }
}
