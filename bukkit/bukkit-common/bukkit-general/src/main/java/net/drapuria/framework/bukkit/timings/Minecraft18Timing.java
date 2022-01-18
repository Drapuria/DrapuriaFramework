package net.drapuria.framework.bukkit.timings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

class Minecraft18Timing extends MCTiming {
    private final Object timing;
    private static Method startTiming;
    private static Method stopTiming;
    private static Method of;

    Minecraft18Timing(Plugin plugin, String name, MCTiming parent) throws InvocationTargetException, IllegalAccessException {
        this.timing = of.invoke((Object)null, plugin, name, parent instanceof Minecraft18Timing ? ((Minecraft18Timing)parent).timing : null);
    }

    public MCTiming startTiming() {
        try {
            if (startTiming != null) {
                startTiming.invoke(this.timing);
            }
        } catch (InvocationTargetException | IllegalAccessException var2) {
        }

        return this;
    }

    public void stopTiming() {
        try {
            if (stopTiming != null) {
                stopTiming.invoke(this.timing);
            }
        } catch (InvocationTargetException | IllegalAccessException var2) {
        }

    }

    static {
        try {
            Class<?> timing = Class.forName("co.aikar.timings.Timing");
            Class<?> timings = Class.forName("co.aikar.timings.Timings");
            startTiming = timing.getDeclaredMethod("startTimingIfSync");
            stopTiming = timing.getDeclaredMethod("stopTimingIfSync");
            of = timings.getDeclaredMethod("of", Plugin.class, String.class, timing);
        } catch (NoSuchMethodException | ClassNotFoundException var2) {
            var2.printStackTrace();
            Bukkit.getLogger().severe("Timings18 failed to initialize correctly. Stuff's going to be broken.");
        }

    }
}
