package net.drapuria.framework.scheduler;


import net.drapuria.framework.scheduler.provider.SchedulerProvider;

public interface ISchedulerService {

    <T extends SchedulerProvider> T registerProvider(Class<T> provider);

    <T extends SchedulerProvider> void unregister(T provider);

    <T extends SchedulerProvider> T getProvider(Class<T> providerClass);

    void shutdown();

}
