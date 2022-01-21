/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DefaultCommand {

    String parameters() default "";

    boolean async() default false;

}
