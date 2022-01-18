package net.drapuria.framework.bukkit.timings;

class EmptyTiming extends MCTiming {
    EmptyTiming() {
    }

    public final MCTiming startTiming() {
        return this;
    }

    public final void stopTiming() {
    }
}
