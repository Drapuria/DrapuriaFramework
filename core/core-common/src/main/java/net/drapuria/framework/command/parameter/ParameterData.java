/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.parameter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

public abstract class ParameterData<P extends Parameter> {

    private final P[] parameters;
    private final int length;


    public ParameterData(P[] parameters) {
        this.parameters = parameters;
        this.length = getParameters().length;
    }

    public P[] getParameters() {
        return parameters;
    }

    public P get(int index) {
        return parameters[index];
    }

    public int getParameterCount() {
        return this.length;
    }

}
