package net.drapuria.framework.beans.component;

import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.details.constructor.BeanParameterDetailsConstructor;
public abstract class ComponentHolder {

    public Object newInstance(Class<?> type) {
        return this.newInstance(this.constructorDetails(type));
    }

    public Object newInstance(BeanParameterDetailsConstructor constructorDetails) {
        try {
            return constructorDetails.newInstance(BeanContext.INSTANCE);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void onEnable(Object instance) {

    }

    // TODO ?
    public void onDisable(Object instance) {

    }

    public abstract Class<?>[] type();

    public BeanParameterDetailsConstructor constructorDetails(Class<?> type) {
        return new BeanParameterDetailsConstructor(type, BeanContext.INSTANCE);
    }

}
