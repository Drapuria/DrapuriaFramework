/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bungee.impl;

import net.drapuria.framework.bungee.Drapuria;
import net.drapuria.framework.task.ITaskScheduler;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class BungeeTaskScheduler implements ITaskScheduler {

    private final Plugin plugin;

    public BungeeTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public int runAsync(Runnable runnable) {
        return Drapuria.getProxy().getScheduler().runAsync(plugin, runnable).getId();
    }

    @Override
    public int runAsyncScheduled(Runnable runnable, long time) {
        return Drapuria.getProxy().getScheduler().schedule(plugin, runnable, time * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runAsyncRepeated(Runnable runnable, long time) {
        return Drapuria.getProxy().getScheduler().schedule(plugin, runnable, 0, time * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runAsyncRepeated(Runnable runnable, long delay, long time) {
        return Drapuria.getProxy().getScheduler().schedule(plugin, runnable, delay * 50, time * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runSync(Runnable runnable) {
        return Drapuria.getProxy().getScheduler().runAsync(plugin, runnable).getId();
    }

    @Override
    public int runScheduled(Runnable runnable, long time) {
        return Drapuria.getProxy().getScheduler().schedule(plugin, runnable, time * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runRepeated(Runnable runnable, long time) {
        return Drapuria.getProxy().getScheduler().schedule(plugin, runnable, 0, time * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runRepeated(Runnable runnable, long delay, long time) {
        return Drapuria.getProxy().getScheduler().schedule(plugin, runnable, delay * 50, time * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public void cancel(int taskId) {
        Drapuria.getProxy().getScheduler().cancel(taskId);
    }
}
