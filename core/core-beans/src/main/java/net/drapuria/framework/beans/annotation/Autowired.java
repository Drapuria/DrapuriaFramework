package net.drapuria.framework.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Autowiring Field for Beans
 *
 * To use @Autowired on a field, the field type must be a Bean
 * And the class it self must also be a bean or do DrapuriaCommon.registerBean() to use it
 *
 * When it's Bean, The field injection will be between PreInitialize and PostInitialize
 * So better to use it when it's PostInitialize
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    Class<?> type() default Void.class;

}
