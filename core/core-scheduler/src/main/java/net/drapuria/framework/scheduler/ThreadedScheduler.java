package net.drapuria.framework.scheduler;

public class ThreadedScheduler<T> extends Scheduler<T>{
    public ThreadedScheduler(long delay) {
        super(delay);
    }

    public ThreadedScheduler(long delay, long period) {
        super(delay, period);
    }

    public ThreadedScheduler(long delay, long period, long iterations) {
        super(delay, period, iterations);
    }
}
