package net.drapuria.framework.services.details.constructor;

import net.drapuria.framework.services.BeanContext;

import java.lang.reflect.Parameter;

public interface BeanParameterDetails {

    Object[] getParameters(BeanContext beanContext);

    Parameter[] getParameters();
}
