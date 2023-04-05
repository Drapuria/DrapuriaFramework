package net.drapuria.framework.beans.annotation;

import net.drapuria.framework.beans.configuration.ConfigurationEnableMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    String value() default "";
    ConfigurationEnableMethod enableMethod() default ConfigurationEnableMethod.ENABLE;
}
