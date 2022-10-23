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
public @interface SubCommand {

    String[] names();

    String parameters() default "";

    String permission() default "";

    boolean async() default false;

    boolean completeAll() default false;

}
