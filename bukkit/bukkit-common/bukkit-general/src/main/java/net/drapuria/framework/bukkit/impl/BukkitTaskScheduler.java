/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl;

import net.drapuria.framework.task.ITaskScheduler;
import org.bukkit.plugin.Plugin;

public class BukkitTaskScheduler implements ITaskScheduler {

    private final Plugin plugin;

    public BukkitTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int runAsync(Runnable runnable) {
        return this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable).getTaskId();
    }

    @Override
    public int runAsyncScheduled(Runnable runnable, long time) {
        return this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, time).getTaskId();
    }

    @Override
    public int runAsyncRepeated(Runnable runnable, long time) {
        return this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, time, time).getTaskId();
    }

    @Override
    public int runAsyncRepeated(Runnable runnable, long delay, long time) {
        return this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay, time).getTaskId();
    }

    @Override
    public int runSync(Runnable runnable) {
        return this.plugin.getServer().getScheduler().runTask(this.plugin, runnable).getTaskId();
    }

    @Override
    public int runScheduled(Runnable runnable, long time) {
        return this.plugin.getServer().getScheduler().runTaskLater(this.plugin, runnable, time).getTaskId();
    }

    @Override
    public int runRepeated(Runnable runnable, long time) {
        return this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, runnable, time, time).getTaskId();
    }

    @Override
    public int runRepeated(Runnable runnable, long delay, long time) {
        return this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, runnable, delay, time).getTaskId();
    }

    @Override
    public void cancel(int taskId) {
        this.plugin.getServer().getScheduler().cancelTask(taskId);
    }
}
