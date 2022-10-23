/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.commands;

import net.drapuria.framework.bukkit.impl.annotation.UseFrameworkPlugin;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.annotations.NewInstance;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.DefaultCommand;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.service.ModuleService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

//@Command(names = {"drapuria"}, useSubCommandsOnly = false)
public class FrameworkCommand extends DrapuriaCommand {

    private final Plugin plugin;

    @NewInstance
    @UseFrameworkPlugin
    public FrameworkCommand(Plugin plugin, ModuleService moduleService) {
        super();
        this.plugin = plugin;
    }

    @DefaultCommand
    public void execute(final DrapuriaPlayer player ){
     //   player.sendActionBar("§fThis ");
    }

}
