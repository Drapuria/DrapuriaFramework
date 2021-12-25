package net.drapuria.framework.bukkit.timings;

public abstract class MCTiming implements AutoCloseable {
    public MCTiming() {
    }

    public abstract MCTiming startTiming();

    public abstract void stopTiming();

    public void close() {
        this.stopTiming();
    }
}
