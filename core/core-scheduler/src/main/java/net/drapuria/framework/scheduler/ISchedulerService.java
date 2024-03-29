/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler;


import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;

public interface ISchedulerService {

    <T extends AbstractSchedulerProvider> T registerProvider(Class<T> provider);

    <T extends AbstractSchedulerProvider> void unregister(T provider);

    <T extends AbstractSchedulerProvider> T getProvider(Class<T> providerClass);

    <T extends AbstractSchedulerProvider> T getProvider(final String providerName);

    Class<? extends AbstractSchedulerProvider>  getProviderClass(final String providerName);

    void shutdown();

}
