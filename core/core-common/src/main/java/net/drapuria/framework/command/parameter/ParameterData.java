/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.parameter;

import java.lang.reflect.Method;
import java.util.Set;

public abstract class ParameterData<P extends Parameter> {

    private final Method method;
    private final P[] parameters;
    private final int length;
    private final Set<String> labels;
    private final int labelSize;


    public ParameterData(Method method, P[] parameters, Set<String> labels) {
        this.method = method;
        this.parameters = parameters;
        this.labels = labels;
        this.length = getParameters().length;
        ;
        this.labelSize = this.labels.size();
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


    public boolean isValidLabel(String label) {
        if (this.labelSize == 0)
            return true;
        return labels.contains(label);
    }
}
