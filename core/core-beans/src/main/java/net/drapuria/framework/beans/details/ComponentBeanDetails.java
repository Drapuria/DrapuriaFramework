package net.drapuria.framework.beans.details;

import net.drapuria.framework.beans.component.ComponentHolder;
import org.jetbrains.annotations.Nullable;

public class ComponentBeanDetails extends GenericBeanDetails {

    private final ComponentHolder componentHolder;

    public ComponentBeanDetails(Class<?> type, @Nullable Object instance, String name, ComponentHolder componentHolder) {
        super(type, instance, name);

        this.componentHolder = componentHolder;
    }

    @Override
    public void onEnable() {
        this.componentHolder.onEnable(this.getInstance());
    }

    @Override
    public void onDisable() {
        this.componentHolder.onDisable(this.getInstance());
    }
}
