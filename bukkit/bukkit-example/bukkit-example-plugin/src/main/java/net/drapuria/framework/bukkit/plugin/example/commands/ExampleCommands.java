/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.plugin.example.commands;

import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.DefaultCommand;
import net.drapuria.framework.command.annotation.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(names = "example", useSubCommandsOnly = false)
public class ExampleCommands extends DrapuriaCommand {

    private final Plugin plugin;

    public ExampleCommands(Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    @DefaultCommand
    public void defaultCommand(final DrapuriaPlayer player) {

    }

    @SubCommand(names = "hello", parameters = "{Spieler}")
  //  @CommandParameter(allowNull = true, wrongParameter = @WrongParameter(type = COMMAND, value = "/help")) // ingame command execution
//    @CommandParameter(allowNull = true, wrongParameter = @WrongParameter(type = LANGSTRING, value = "wrong-parameter")) // lang string
  //  @CommandParameter(allowNull = true, wrongParameter = @WrongParameter(type = METHOD, value = "defaultCommand")) // hier sucht sich das framework die methode als erstes (muss in der cmd class sein)
    public void helloCommand(Player player, Player target/*, @CommandParameter(allowNull = true, wrongParameter = @WrongParameter(type = COMMAND, value = "/help")) Integer speed*/) {

        // /hello Benutze /hello <Spieler>
        player.sendMessage("Spieler: " + target.getName());
    }

}