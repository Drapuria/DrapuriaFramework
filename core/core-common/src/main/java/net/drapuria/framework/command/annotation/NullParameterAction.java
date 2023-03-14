package net.drapuria.framework.command.annotation;

import net.drapuria.framework.command.parameter.NullAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface NullParameterAction {

    String method() default "";

    String errorString() default "";

    String translateableErroString() default "";

}
