package net.drapuria.framework.scheduler;

import lombok.SneakyThrows;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.scheduler.provider.SchedulerProvider;

import java.util.HashMap;
import java.util.Map;

@Service(name = "schedulerService")
public class SchedulerService implements ISchedulerService {

    private final Map<Class<? extends SchedulerProvider>, SchedulerProvider> providers = new HashMap<>();

    public static SchedulerService getService;

    public SchedulerService() {
        getService = this;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public <T extends SchedulerProvider> T registerProvider(Class<T> provider) {
        if (this.providers.containsKey(provider)) {
            FrameworkMisc.PLATFORM.getLogger().error("Provider " + provider.getSimpleName() + " already initialized");
            return null;
        }
        T schedulerProvider = (T) provider.newInstance();
        this.providers.put(provider, schedulerProvider);
        return schedulerProvider;
    }

    @Override
    public <T extends SchedulerProvider> void unregister(T provider) {
        if (provider == null)
            return;
        providers.remove(provider.getClass());
        provider.shutdown();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SchedulerProvider> T getProvider(Class<T> providerClass) {
        if (providerClass == null) {
            FrameworkMisc.PLATFORM.getLogger().error("Provider class cannot be null!");
            return null;
        }
        if (this.providers.containsKey(providerClass)) {
            return (T) this.providers.get(providerClass);
        }
        return registerProvider(providerClass);
    }

    @Override
    public void shutdown() {
        providers.values().forEach(this::unregister);
    }
}
