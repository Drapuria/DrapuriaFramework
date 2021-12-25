package net.drapuria.framework.services.details.constructor;

import lombok.Getter;
import net.drapuria.framework.services.BeanContext;

import java.lang.reflect.Parameter;

public class BeanParameterDetailsAbstract implements BeanParameterDetails {

    @Getter
    protected Parameter[] parameters;

    @Override
    public Object[] getParameters(BeanContext beanContext) {
        if (this.parameters == null) {
            throw new IllegalArgumentException("No parameters found!");
        }

        Object[] parameters = new Object[this.parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Object bean = beanContext.getBean(this.parameters[i].getType());
            if (bean == null) {
                throw new IllegalArgumentException("Couldn't find bean " + this.parameters[i].getName() + "!");
            }

            parameters[i] = bean;
        }

        return parameters;
    }
}
