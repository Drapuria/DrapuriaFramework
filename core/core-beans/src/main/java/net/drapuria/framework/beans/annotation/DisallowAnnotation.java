package net.drapuria.framework.beans.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisallowAnnotation {

    Class<? extends Annotation>[] value();

}
