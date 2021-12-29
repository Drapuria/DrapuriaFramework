package net.drapuria.framework.command.meta;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class CommandMeta<E> {

    protected String commandName;
    protected String commandDescription;

    protected String[] commandAliases;
    protected boolean asyncExecution;

    public CommandMeta(String commandName, String commandDescription) {
        this.commandName = null;
        this.commandDescription = null;
    }

    public abstract boolean canAccess(E executor);

}
