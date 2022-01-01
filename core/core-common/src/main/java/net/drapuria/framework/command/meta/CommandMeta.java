package net.drapuria.framework.command.meta;

import lombok.Getter;
import net.drapuria.framework.command.parameter.ParameterData;

import java.lang.reflect.Method;
import java.util.Map;

@Getter
public abstract class CommandMeta<E, T extends ParameterData<?>> {

    protected Method method;
    protected boolean isAsyncDefaultCommand;
    protected final Object instance;
    protected T parameterData;
    protected String commandName;
    protected String commandDescription;

    protected String[] commandAliases;

    public CommandMeta(Object instance, String commandName, String commandDescription) {
        this.commandName = commandName;
        this.parameterData = null;
        this.commandDescription = commandDescription;
        this.instance = instance;
    }

    public abstract boolean canAccess(E executor);

    public abstract void execute(E executor, String[] params);

}
