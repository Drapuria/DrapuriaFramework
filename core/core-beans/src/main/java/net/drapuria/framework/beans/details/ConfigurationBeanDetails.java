package net.drapuria.framework.beans.details;

import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.configuration.AbstractConfigurationProvider;
import net.drapuria.framework.beans.configuration.ConfigurationEnableMethod;
import net.drapuria.framework.beans.configuration.ConfigurationProviderRegistry;
import net.drapuria.framework.beans.configuration.GenericConfigurationProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public class ConfigurationBeanDetails extends GenericBeanDetails {

    private final AbstractConfigurationProvider<?> configurationProvider;
    private final ConfigurationEnableMethod enableMethod;

    public ConfigurationBeanDetails(Class<?> type, String name, ConfigurationEnableMethod enableMethod) {
        super(type, name);
        this.configurationProvider = ConfigurationProviderRegistry.findProvider(type);
        this.enableMethod = enableMethod;
    }


    public void enableConfiguration() {
        BeanContext.LOGGER.info("ENABLING CONFIGURATION");
        this.configurationProvider.internalOnEnable(getInstance());
    }

    public void postEnableConfiguration() {
        BeanContext.LOGGER.info("POST ENABLING CONFIGURATION");
        this.configurationProvider.internalOnPostEnable(getInstance());
    }

    @Override
    public void call(Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException {
        if (annotation == PreInitialize.class && this.isStage(ActivationStage.PRE_INIT_CALLED)) return;
        if (annotation == PreInitialize.class && this.isStage(ActivationStage.POST_INIT_CALLED)) return;
        if (annotation == PreInitialize.class && (!(configurationProvider instanceof GenericConfigurationProvider)
                || enableMethod == ConfigurationEnableMethod.ENABLE)) enableConfiguration();
        else if (annotation == PostInitialize.class && (!(configurationProvider instanceof GenericConfigurationProvider)
                || enableMethod == ConfigurationEnableMethod.POST_ENABLE)) postEnableConfiguration();

        super.changeStage(annotation);
    }
}