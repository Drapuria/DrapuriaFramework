/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm;

import net.drapuria.framework.ObjectSerializer;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomSerializer {

    Class<? extends ObjectSerializer<?, ?>> value();

}
