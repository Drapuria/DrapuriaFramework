package net.drapuria.framework.bukkit.timings;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


import net.drapuria.framework.services.PreInitialize;
import net.drapuria.framework.services.Service;
import org.bukkit.plugin.Plugin;

@Service(name = "Timings")
public class TimingService {
    private TimingType timingType;
    private final Map<String, MCTiming> timingCache = new HashMap(0);

    public TimingService() {
    }

    @PreInitialize
    public void preInit() {
        if (this.timingType == null) {
            try {
                Class<?> clazz = Class.forName("co.aikar.timings.Timing");
                Method startTiming = clazz.getMethod("startTiming");
                if (startTiming.getReturnType() != clazz) {
                    this.timingType = TimingType.MINECRAFT_18;
                } else {
                    this.timingType = TimingType.MINECRAFT;
                }
            } catch (NoSuchMethodException | ClassNotFoundException var4) {
                try {
                    Class.forName("org.spigotmc.CustomTimingsHandler");
                    this.timingType = TimingType.SPIGOT;
                } catch (ClassNotFoundException var3) {
                    this.timingType = TimingType.EMPTY;
                }
            }
        }

    }

    public MCTiming ofStart(Plugin plugin, String name) {
        return this.ofStart(plugin, name, (MCTiming)null);
    }

    public MCTiming ofStart(Plugin plugin, String name, MCTiming parent) {
        return this.of(plugin, name, parent).startTiming();
    }

    public MCTiming of(Plugin plugin, String name) {
        return this.of(plugin, name, (MCTiming)null);
    }

    public MCTiming of(Plugin plugin, String name, MCTiming parent) {
        if (this.timingType.useCache()) {
            synchronized(this.timingCache) {
                String lowerKey = name.toLowerCase();
                MCTiming timing = (MCTiming)this.timingCache.get(lowerKey);
                if (timing == null) {
                    timing = this.timingType.newTiming(plugin, name, parent);
                    this.timingCache.put(lowerKey, timing);
                }

                return timing;
            }
        } else {
            return this.timingType.newTiming(plugin, name, parent);
        }
    }
}
