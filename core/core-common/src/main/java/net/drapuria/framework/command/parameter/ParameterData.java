package net.drapuria.framework.command.parameter;

public abstract class ParameterData<P extends Parameter> {

    private final P[] parameters;

    private final int length;

    public ParameterData(P[] parameters) {
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

}
