package net.drapuria.framework.beans.configuration;

import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.ConfigurationProvider;
import net.drapuria.framework.beans.exception.ConfigurationException;
import net.drapuria.framework.util.TypeResolver;
import net.drapuria.framework.util.entry.Entry;
import net.drapuria.framework.util.entry.EntryArrayList;
import org.imanity.framework.reflect.ReflectLookup;

import java.lang.reflect.InvocationTargetException;

public class ConfigurationProviderRegistry {

    private static final GenericConfigurationProvider GENERIC_CONFIGURATION_PROVIDER = new GenericConfigurationProvider();

    private static final EntryArrayList<Class<?>, AbstractConfigurationProvider<?>> PROVIDERS = new EntryArrayList<>();

    public static void scanProviders(BeanContext beanContext, ReflectLookup reflectLookup) {
        for (Class<?> configurationClass : reflectLookup.findAnnotatedClasses(ConfigurationProvider.class)) {

            final Class<?> configurationType = TypeResolver.resolveRawArguments(AbstractConfigurationProvider.class, configurationClass)[0];
            if (!configurationType.isInterface())
                throw new RuntimeException(new ConfigurationException("Configuration class has to be the type of an interface!"));
            if (PROVIDERS.contains(configurationType)) {
                beanContext.LOGGER.warn("Configuration Provider for class " + configurationClass.getName() + " already registered!");
                continue;
            }
            try {
                Object instance = configurationClass.getConstructor().newInstance();
                PROVIDERS.add(configurationType, (AbstractConfigurationProvider<?>) instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                beanContext.LOGGER.error("Could not register Configuration Provider", e);
            }
        }
    }

    public static AbstractConfigurationProvider<?> findProvider(Class<?> configurationClass) {
        for (Entry<Class<?>, AbstractConfigurationProvider<?>> entry : PROVIDERS) {
            if (entry.getKey().isAssignableFrom(configurationClass))
                return entry.getValue();
        }
        return GENERIC_CONFIGURATION_PROVIDER;
    }
}