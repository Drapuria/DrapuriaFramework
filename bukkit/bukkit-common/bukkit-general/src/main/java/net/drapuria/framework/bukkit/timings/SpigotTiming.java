/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.timings;

import org.bukkit.Bukkit;
import org.spigotmc.CustomTimingsHandler;

class SpigotTiming extends MCTiming {
    private final CustomTimingsHandler timing;

    SpigotTiming(String name) {
        this.timing = new CustomTimingsHandler(name);
    }

    public MCTiming startTiming() {
        if (Bukkit.isPrimaryThread()) {
            this.timing.startTiming();
        }

        return this;
    }

    public void stopTiming() {
        if (Bukkit.isPrimaryThread()) {
            this.timing.stopTiming();
        }

    }
}
