/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.parameter;

import java.lang.reflect.Method;

public abstract class ParameterData<P extends Parameter> {

    private final Method method;
    private final P[] parameters;
    private final int length;

    public ParameterData(Method method, P[] parameters) {
        this.method = method;
        this.parameters = parameters;
        this.length = getParameters().length;;
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

    public Method getMethod() {
        return method;
    }
}
