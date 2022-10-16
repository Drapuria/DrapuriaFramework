/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.parameter;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Parameter {

    public static final String CURRENT_SELF = UUID.randomUUID().toString();


    private final Class<?> classType;
    private final String parameter;

    private final String defaultValue;
    private final boolean wildcard;
    private final java.lang.reflect.Parameter javaParameter;

    public Parameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard, java.lang.reflect.Parameter javaParameter) {
        this.classType = classType;
        this.parameter = parameter;
        this.defaultValue = defaultValue.replace("self", CURRENT_SELF);
        this.wildcard = wildcard;
        this.javaParameter = javaParameter;
    }
}
