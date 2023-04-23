/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command;

import com.google.common.collect.ImmutableSet;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.bukkit.impl.command.tabcompletion.DrapuriaTabCompletion;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// 1.8 IMPLEMENTATION

@CommandMapImpl
public class DrapuriaCommandMap extends SimpleCommandMap implements ICommandMap {

    private final BukkitCommandProvider commandProvider;

    private final DrapuriaTabCompletion tabCompletion;

    public DrapuriaCommandMap(Server server, BukkitCommandProvider commandProvider) {
        super(server);
        this.commandProvider = commandProvider;
        this.tabCompletion = new DrapuriaTabCompletion(commandProvider, this);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        return this.tabComplete(sender, cmdLine, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        return this.tabCompletion.tabComplete(sender, cmdLine, location);
    }

    @Override
    public void unregisterDrapuriaCommand(Command command) {
        command.unregister(this);
    }

    @Override
    public List<String> spigotTabComplete(CommandSender sender, String cmdLine, Location location) {
        return super.tabComplete(sender, cmdLine, location);
    }
}