package net.drapuria.framework.bukkit.impl.scheduler.provider;

import net.drapuria.framework.bukkit.impl.scheduler.pool.BukkitAsyncSchedulerPool;
import net.drapuria.framework.bukkit.impl.task.DrapuriaAsyncBukkitTask;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.provider.AsyncAbstractSchedulerProvider;
import net.drapuria.framework.task.TaskAlreadyStartedException;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class BukkitAsyncSchedulerProvider extends AsyncAbstractSchedulerProvider {

    private static final MinecraftServer minecraftServer;

    static {
        minecraftServer = MinecraftServer.getServer();
    }


    @Override
    protected void initPool() throws TaskAlreadyStartedException {
        (super.delayTask = new DrapuriaAsyncBukkitTask()).start(0, 1, this::tickPool);
    }

    @Override
    protected void createSchedulerPool(long period) {
        super.schedulerPools.put(period, new BukkitAsyncSchedulerPool(period, this));
    }

    @Override
    public void addOrCreatePool(Scheduler<?> scheduler) {
        minecraftServer.postToMainThread(() -> {
            addSchedulerToPoolSync(scheduler);
        });
    }

    private void addSchedulerToPoolSync(Scheduler<?> scheduler) {
        addToSuper(scheduler);
    }
}
