/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.annotation;

import net.drapuria.framework.command.context.permission.UnknownPermissionContext;
import net.drapuria.framework.command.context.permission.PermissionContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String[] names();

    String permission() default "";

    Class<? extends PermissionContext> permissionContext() default UnknownPermissionContext.class;

    String description() default "";

}
