/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans.annotation;

import net.drapuria.framework.beans.ServiceDependencyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyType {

    ServiceDependencyType value() default ServiceDependencyType.FORCE;

}
