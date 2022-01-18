package net.drapuria.framework.bukkit.impl.task;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import org.bukkit.scheduler.BukkitRunnable;

public final class DrapuriaAsyncBukkitTask extends DrapuriaBukkitTask {

    @Override
    public void start(long delay, long period, Runnable runnable) throws TaskAlreadyStartedException {
        if (super.task != null) {
            throw new TaskAlreadyStartedException("Task already started with id" + task.getTaskId());
        }
        super.task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimerAsynchronously(Drapuria.PLUGIN, delay, period);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
