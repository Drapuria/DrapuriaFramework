/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.meta;

import lombok.Getter;
import net.drapuria.framework.command.parameter.ParameterData;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CommandMeta<E, T extends ParameterData<?>> {

    protected boolean isAsyncDefaultCommand;
    protected final Object instance;
    protected List<T> parameterDatas;
    protected String commandName;
    protected String commandDescription;

    protected String[] commandAliases;

    public CommandMeta(Object instance, String commandName, String commandDescription) {
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.instance = instance;
    }

    public abstract boolean canAccess(E executor);

    public abstract void execute(E executor, String[] params);

}
