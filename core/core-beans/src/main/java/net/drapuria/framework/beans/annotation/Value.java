package net.drapuria.framework.beans.annotation;

import net.drapuria.framework.beans.property.DefaultPropertySource;
import net.drapuria.framework.beans.property.PropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    String value();

    String defaultValue() default "";

    Class<? extends PropertySource> propertySource() default DefaultPropertySource.class;

}