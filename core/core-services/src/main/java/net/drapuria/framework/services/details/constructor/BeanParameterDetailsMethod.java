package net.drapuria.framework.services.details.constructor;

import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.services.BeanContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Getter
public class BeanParameterDetailsMethod extends BeanParameterDetailsAbstract {

    private final Method method;

    @SneakyThrows
    public BeanParameterDetailsMethod(Method method, BeanContext beanContext) {
        this.method = method;

        this.parameters = this.method.getParameters();
        for (Parameter parameter : this.parameters) {
            if (!beanContext.isBean(parameter.getType())) {
                throw new IllegalArgumentException("The type " + parameter.getType().getName() + " is not a bean!, it's not supposed to be in bean method!");
            }
        }
    }

    public Object invoke(Object instance, BeanContext beanContext) throws InvocationTargetException, IllegalAccessException {
        Object[] parameters = this.getParameters(beanContext);

        return this.method.invoke(instance, parameters);
    }

}
