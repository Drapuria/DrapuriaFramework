package net.drapuria.framework.bukkit.impl.scheduler.factory;

import net.drapuria.framework.bukkit.impl.scheduler.DrapuriaScheduler;
import net.drapuria.framework.bukkit.impl.scheduler.provider.BukkitAsyncSchedulerProvider;
import net.drapuria.framework.bukkit.impl.scheduler.provider.BukkitSchedulerProvider;
import net.drapuria.framework.scheduler.SchedulerService;
import net.drapuria.framework.scheduler.factory.AbstractSchedulerFactory;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;

public class BukkitSchedulerFactory<T> extends AbstractSchedulerFactory<T, DrapuriaScheduler<T>> {

    public AbstractSchedulerFactory<T, DrapuriaScheduler<T>>  async() {
        return provider(BukkitAsyncSchedulerProvider.class);
    }

    public AbstractSchedulerFactory<T, DrapuriaScheduler<T>> sync() {
        return provider(BukkitSchedulerProvider.class);
    }

    @Override
    public DrapuriaScheduler<T> build() {
        super.buildInternal();
        DrapuriaScheduler<T> scheduler = new DrapuriaScheduler<>(delay, period, iterations);
        scheduler.getTimedActions().putAll(super.scheduledEvents);
        scheduler.getRepeatedActions().addAll(super.repeatedActions);
        scheduler.setSupplier(super.supplier);
        final AbstractSchedulerProvider provider = SchedulerService.getService.getProvider(super.provider);
        provider.addSchedulerToPool(scheduler);
        return scheduler;
    }
}