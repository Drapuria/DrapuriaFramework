package net.drapuria.framework.bukkit.timings;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.plugin.Plugin;

enum TimingType {
    SPIGOT(true) {
        MCTiming newTiming(Plugin plugin, String command, MCTiming parent) {
            return new SpigotTiming(command);
        }
    },
    MINECRAFT {
        MCTiming newTiming(Plugin plugin, String command, MCTiming parent) {
            return new MinecraftTiming(plugin, command, parent);
        }
    },
    MINECRAFT_18 {
        MCTiming newTiming(Plugin plugin, String command, MCTiming parent) {
            try {
                return new Minecraft18Timing(plugin, command, parent);
            } catch (IllegalAccessException | InvocationTargetException var5) {
                return new EmptyTiming();
            }
        }
    },
    EMPTY;

    private final boolean useCache;

    public boolean useCache() {
        return this.useCache;
    }

    private TimingType() {
        this(false);
    }

    private TimingType(boolean useCache) {
        this.useCache = useCache;
    }

    MCTiming newTiming(Plugin plugin, String command, MCTiming parent) {
        return new EmptyTiming();
    }
}
