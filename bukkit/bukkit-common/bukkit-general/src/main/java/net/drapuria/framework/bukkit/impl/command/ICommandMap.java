package net.drapuria.framework.bukkit.impl.command;


import org.bukkit.command.Command;

public interface ICommandMap {

    Command getCommand(String name);

    boolean register(String prefix, Command command);

    void unregisterDrapuriaCommand(Command command);

}
