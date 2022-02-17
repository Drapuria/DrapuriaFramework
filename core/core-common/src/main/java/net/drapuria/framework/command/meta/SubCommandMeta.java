/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.meta;

import lombok.Getter;
import net.drapuria.framework.command.parameter.ParameterData;

import java.lang.reflect.Method;

@Getter
public abstract class SubCommandMeta<E, T extends ParameterData<?>> {

    protected final CommandMeta<E, ?> commandMeta;
    protected final T parameterData;
    protected final String[] aliases;

    protected final Object instance;
    protected final Method method;

    protected final String defaultAlias;
    protected final String parameterString;
    protected boolean asyncExecution;

    public SubCommandMeta(CommandMeta<E, ?> commandMeta, T parameterData, String[] aliases, Object instance, Method method, String parameterString) {
        this.commandMeta = commandMeta;
        this.parameterData = parameterData;
        this.aliases = aliases;
        this.instance = instance;
        this.method = method;
        this.defaultAlias = aliases.length == 0 ? "" : aliases[0];
        for (int i = 0; i < aliases.length; i++)
            aliases[i] = aliases[i].toLowerCase();
        this.parameterString = parameterString;
    }

    public abstract boolean execute(E executor, String[] params);

    public abstract boolean canAccess(E executor);

}
