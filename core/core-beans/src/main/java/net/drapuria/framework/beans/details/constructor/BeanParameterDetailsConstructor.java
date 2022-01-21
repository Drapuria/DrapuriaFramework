/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans.details.constructor;

import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.beans.annotation.BeanConstructor;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.util.AccessUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

@Getter
public class BeanParameterDetailsConstructor extends BeanParameterDetailsAbstract {

    private final Class<?> type;
    private final Constructor<?> constructor;

    @SneakyThrows
    public BeanParameterDetailsConstructor(Class<?> type, BeanContext beanContext) {
        this.type = type;

        Constructor<?> constructorRet = null;
        int priorityRet = -1;

        for (Constructor<?> constructor : this.type.getDeclaredConstructors()) {
            AccessUtil.setAccessible(constructor);

            int priority = -1;
            BeanConstructor annotation = constructor.getAnnotation(BeanConstructor.class);
            if (annotation != null) {
                priority = annotation.priority();
            }

            if (constructorRet == null || priorityRet < priority) {
                constructorRet = constructor;
                priorityRet = priority;
            }
        }

        this.constructor = constructorRet;
        this.parameters = this.constructor.getParameters();
        for (Parameter parameter : this.parameters) {
            if (!beanContext.isBean(parameter.getType())) {
                throw new IllegalArgumentException("The type " + parameter.getType().getName() + " it's not supposed to be in bean constructor!");
            }
        }
    }

    public Object newInstance(BeanContext beanContext) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.constructor.newInstance(this.getParameters(beanContext));
    }

}
