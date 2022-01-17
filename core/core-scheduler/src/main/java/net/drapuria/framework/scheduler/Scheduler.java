package net.drapuria.framework.scheduler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.scheduler.action.RepeatedAction;
import net.drapuria.framework.scheduler.helper.SchedulerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <T> The scheduled object
 */
@Getter
public class Scheduler<T> {

    protected final Map<Long, Consumer<T>> timedActions = new HashMap<>();
    protected final List<RepeatedAction<T>> repeatedActions = new ArrayList<>();

    @Setter
    private long delay;
    private long period;
    @Getter
    protected long iterations;

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
        if (iterations != -1)
            iterations--;
        long expiredTime = System.currentTimeMillis() - getStartTime();
        try {
            repeatedActions.forEach(repeatedAction -> {
                if (repeatedAction.isAlways()) {
                    if (!repeatedAction.isLastTick() && iterations == 0) return;
                    repeatedAction.getAction().accept(expiredTime, getSupplier().get());
                } else if (iterations % repeatedAction.getDivision() == repeatedAction.getRemainder())
                    repeatedAction.getAction().accept(expiredTime, getSupplier().get());
            });
            Consumer<T> consumer = parseAction(iterations);
            if (consumer != null)
                consumer.accept(supplier.get());
        } catch (Exception ignored) {
        }
        return iterations == 0;
    }

    public Consumer<T> parseAction(long time) {
        return timedActions.get(time);
    }

    public Consumer<T> parseAction(Timestamp timestamp) {
        return parseAction(SchedulerHelper.convertTimestampToIteration(timestamp, this.iterations));
    }
}
