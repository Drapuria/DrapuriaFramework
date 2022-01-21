/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.task;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.task.ITask;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import net.drapuria.framework.task.TaskNotStartedException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DrapuriaBukkitTask implements ITask {

    protected BukkitTask task;

    @Override
    public void shutdown() throws TaskNotStartedException {
        if (this.task == null)
            throw new TaskNotStartedException("Task is not started");
        this.task.cancel();
        this.task = null;
    }

    @Override
    public void start(long delay, long period, Runnable runnable) throws TaskAlreadyStartedException {
        if (this.task != null) {
            throw new TaskAlreadyStartedException("Task already started with id " + task.getTaskId());
        }
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(Drapuria.PLUGIN, delay, period);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}