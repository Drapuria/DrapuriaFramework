package net.drapuria.framework.cooldown;

import lombok.Getter;

@Getter
public class Cooldown {

    private long duration;
    private final long start;
    private long endTime;

    protected Cooldown(long duration) {
        this.duration = duration;
        this.start = System.currentTimeMillis();
        this.endTime = this.start + this.duration;
    }

    public boolean hasEnded() {
        return endTime <= System.currentTimeMillis();
    }

    public void extend(long duration) {
        this.endTime += duration;
        this.duration += duration;
    }

    public void reduce(long duration) {
        this.endTime -= duration;
        this.duration -= duration;
    }

    public static Cooldown of(long duration) {
        return new Cooldown(duration);
    }

    public static Cooldown byEndTime(long endTime) {
        final long current = System.currentTimeMillis();
        if (endTime < current)
            throw new IllegalStateException("Endtime has already expired");
        return new Cooldown(endTime - current);
    }
}