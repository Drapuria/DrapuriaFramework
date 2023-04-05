package net.drapuria.framework.beans.details;

import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.Bean;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.configuration.AbstractConfigurationProvider;
import net.drapuria.framework.beans.configuration.ConfigurationEnableMethod;
import net.drapuria.framework.beans.configuration.ConfigurationProviderRegistry;
import net.drapuria.framework.beans.configuration.GenericConfigurationProvider;
import net.drapuria.framework.beans.details.constructor.BeanParameterDetailsMethod;
import net.drapuria.framework.beans.exception.ServiceAlreadyExistsException;
import org.imanity.framework.reflect.ReflectLookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        if (annotation == PostInitialize.class && this.isStage(ActivationStage.POST_INIT_CALLED)) return;
        if (annotation == PreInitialize.class && (!(configurationProvider instanceof GenericConfigurationProvider)
                || enableMethod == ConfigurationEnableMethod.ENABLE)) enableConfiguration();
        else if (annotation == PostInitialize.class && (!(configurationProvider instanceof GenericConfigurationProvider)
                || enableMethod == ConfigurationEnableMethod.POST_ENABLE)) postEnableConfiguration();

        super.changeStage(annotation);
    }

    public List<BeanDetails> registerBeans(BeanContext beanContext, ReflectLookup reflectLookup) {
        final List<BeanDetails> results = new ArrayList<>();
        for (Method beanMethod : reflectLookup.findAnnotatedInstanceMethods(Bean.class, getType())) {
            if (beanMethod.getReturnType() == void.class)
                continue;
            Bean bean = beanMethod.getAnnotation(Bean.class);
            if (bean == null)
                continue;
            BeanParameterDetailsMethod detailsMethod = new BeanParameterDetailsMethod(beanMethod, beanContext);
            final Object instance;
            try {
                instance = detailsMethod.invoke(getInstance(), beanContext);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }


            String name = bean.name();
            if (name.isEmpty()) {
                name = instance.getClass().toString();
            }

            if (beanContext.getBeanByName(name) == null) {

                BeanDetails beanDetails = new DependenciesBeanDetails(instance.getClass(), instance, name, Arrays.stream(detailsMethod.getParameters()).map(type -> beanContext.getBeanDetails(type.getType())).filter(Objects::nonNull).map(BeanDetails::getName).toArray(String[]::new));

                beanContext.log("Found " + name + " with type " + instance.getClass().getSimpleName() + ", Registering it as bean...");

                beanContext.attemptBindPlugin(beanDetails);
                beanContext.registerBean(beanDetails, false);
                results.add(beanDetails);
            } else {
                new ServiceAlreadyExistsException(name).printStackTrace();
            }
        }
        return results;
    }
}