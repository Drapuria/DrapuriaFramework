/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnTransformer {

    String read() default "";

    String write() default "";

}
