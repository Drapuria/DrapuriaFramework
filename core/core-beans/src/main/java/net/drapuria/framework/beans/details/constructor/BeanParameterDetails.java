/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans.details.constructor;

import net.drapuria.framework.beans.BeanContext;

import java.lang.reflect.Parameter;

public interface BeanParameterDetails {

    Object[] getParameters(BeanContext beanContext);

    Parameter[] getParameters();
}
