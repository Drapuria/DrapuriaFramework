/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.parameter;

import lombok.Getter;
import net.drapuria.framework.command.annotation.NullParameterAction;

import java.util.UUID;

@Getter
public class Parameter {

    public static final String CURRENT_SELF = UUID.randomUUID().toString();


    private final Class<?> classType;
    private final String parameter;

    private final String defaultValue;
    private final boolean wildcard;
    private final boolean isAllowNull;
    private final java.lang.reflect.Parameter javaParameter;
    private final NullParameterAction nullParameterAction;

    public Parameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard, boolean isAllowNull, java.lang.reflect.Parameter javaParameter, final NullParameterAction nullParameterAction) {
        this.classType = classType;
        this.parameter = parameter;
        this.defaultValue = defaultValue.replace("self", CURRENT_SELF);
        this.wildcard = wildcard;
        this.isAllowNull = isAllowNull;
        this.javaParameter = javaParameter;
        this.nullParameterAction = nullParameterAction;
    }
}
