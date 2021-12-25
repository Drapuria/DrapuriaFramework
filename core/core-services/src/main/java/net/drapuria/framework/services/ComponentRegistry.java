package net.drapuria.framework.services;

import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.services.details.ComponentBeanDetails;
import net.drapuria.framework.util.entry.Entry;
import net.drapuria.framework.util.entry.EntryArrayList;
import org.imanity.framework.reflect.ReflectLookup;


import java.util.ArrayList;
import java.util.List;

public class ComponentRegistry {

    private static final EntryArrayList<Class<?>, ComponentHolder> COMPONENT_HOLDERS = new EntryArrayList<>();

    public static void registerComponentHolder(ComponentHolder componentHolder) {
        for (Class<?> type : componentHolder.type()) {
            COMPONENT_HOLDERS.add(type, componentHolder);
        }
    }

    public static ComponentHolder getComponentHolder(Class<?> type) {

        for (Entry<Class<?>, ComponentHolder> entry : COMPONENT_HOLDERS) {
            if (entry.getKey().isAssignableFrom(type)) {
                return entry.getValue();
            }
        }

        return null;

    }

    static void registerComponentHolders() {
        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public Class<?>[] type() {
                return new Class[] {Thread.class};
            }

            @Override
            public void onEnable(Object instance) {
                Thread thread = (Thread) instance;

                FrameworkMisc.TASK_SCHEDULER.runSync(thread::start); // Don't start Immediately
            }
        });
    }

    public static List<ComponentBeanDetails> scanComponents(BeanContext beanContext, ReflectLookup reflectLookup) {
        List<ComponentBeanDetails> components = new ArrayList<>();

        for (Class<?> type : reflectLookup.findAnnotatedClasses(Component.class)) {
            try {
                Component component = type.getAnnotation(Component.class);

                ComponentHolder componentHolder = ComponentRegistry.getComponentHolder(type);
                if (componentHolder == null) {
                    if (component.throwIfNotRegistered()) {
                        BeanContext.LOGGER.error("No ComponentHolder was registered for class " + type.getName() + "!");
                    }
                    continue;
                }

                Object instance = componentHolder.newInstance(type);

                if (instance != null) {
                    final ComponentBeanDetails beanDetails = beanContext.registerComponent(instance, type, componentHolder);
                    if (beanDetails != null) {
                        components.add(beanDetails);
                    }
                }
            } catch (Throwable throwable) {
                BeanContext.LOGGER.error("Something wrong will scanning component for " + type.getName(), throwable);
            }
        }

        return components;
    }

}
