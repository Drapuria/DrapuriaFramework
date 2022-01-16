package net.drapuria.framework.bukkit.impl.scheduler.factory;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.drapuria.framework.bukkit.impl.scheduler.DrapuriaScheduler;
import net.drapuria.framework.bukkit.impl.scheduler.provider.BukkitAsyncSchedulerProvider;
import net.drapuria.framework.bukkit.impl.scheduler.provider.BukkitSchedulerProvider;
import net.drapuria.framework.scheduler.SchedulerService;
import net.drapuria.framework.scheduler.factory.AbstractSchedulerFactory;
import net.drapuria.framework.scheduler.provider.SchedulerProvider;

public class BukkitSchedulerFactory<T> extends AbstractSchedulerFactory<T, DrapuriaScheduler<T>> {

    @Setter
    @Accessors(fluent = true, chain = true)
    private Class<? extends SchedulerProvider> provider = BukkitSchedulerProvider.class;

    public BukkitSchedulerFactory<T> async() {
        return provider(BukkitAsyncSchedulerProvider.class);
    }

    public BukkitSchedulerFactory<T> sync() {
        return provider(BukkitSchedulerProvider.class);
    }

    @Override
    public DrapuriaScheduler<T> build() {
        super.buildInternal();
        DrapuriaScheduler<T> scheduler = new DrapuriaScheduler<>(delay, period, iterations);
        scheduler.getTimedActions().putAll(super.scheduledEvents);
        scheduler.getRepeatedActions().addAll(super.repeatedActions);
        scheduler.setSupplier(super.supplier);
        final SchedulerProvider provider = SchedulerService.getService.getProvider(this.provider);
        provider.addSchedulerToPool(scheduler);
        return scheduler;
    }
}