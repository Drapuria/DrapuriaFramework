package net.drapuria.framework.configuration.yaml.annotation;

import net.drapuria.framework.configuration.yaml.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a custom conversion mechanism is used to convert the
 * annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Convert {
    Class<? extends Converter<?, ?>> value();
}
