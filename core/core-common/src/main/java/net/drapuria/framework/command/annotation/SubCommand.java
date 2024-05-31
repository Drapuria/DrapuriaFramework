/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.annotation;

import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.context.permission.UnknownPermissionContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

    String[] labels() default {};

    String[] names();

    String[] parameters() default "";

    String permission() default "";

    Class<? extends PermissionContext> permissionContext() default UnknownPermissionContext.class;

    boolean async() default false;

    boolean completeAll() default false;

}
