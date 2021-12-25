package net.drapuria.framework.services;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisallowAnnotation {

    Class<? extends Annotation>[] value();

}
