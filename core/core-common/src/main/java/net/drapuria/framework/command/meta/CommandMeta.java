/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.meta;

import lombok.Getter;
import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.executor.ExecutorData;
import net.drapuria.framework.command.parameter.Parameter;
import net.drapuria.framework.command.parameter.ParameterData;

import java.util.List;

@Getter
public abstract class CommandMeta<E, P extends Parameter, T extends ExecutorData<E, P>> {

    protected boolean isAsyncDefaultCommand;
    protected final Object instance;
    protected List<T> executorData;
    protected String commandName;
    protected String commandDescription;
    protected String[] commandAliases;
    protected PermissionContext<E> permissionContext;

    public CommandMeta(Object instance, String commandName, String commandDescription, PermissionContext<E> permissionContext) {
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.instance = instance;
        this.permissionContext = permissionContext;
    }

    public abstract boolean canAccess(E executor);

    public abstract void execute(E executor, String label, String[] params);

}
