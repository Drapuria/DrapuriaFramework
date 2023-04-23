/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command;


import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommandMap {

    Command getCommand(String name);

    boolean register(String prefix, Command command);

    void unregisterDrapuriaCommand(Command command);

    List<String> spigotTabComplete(CommandSender sender, String cmdLine, Location location);

}