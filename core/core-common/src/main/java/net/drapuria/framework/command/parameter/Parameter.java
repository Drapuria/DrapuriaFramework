package net.drapuria.framework.command.parameter;

import lombok.Getter;

@Getter
public class Parameter {

    private final Class<?> classType;
    private final String parameter;

    private final String defaultValue;
    private final boolean wildcard;

    public Parameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard) {
        this.classType = classType;
        this.parameter = parameter;
        this.defaultValue = defaultValue;
        this.wildcard = wildcard;
    }
}
