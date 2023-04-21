/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.test;

import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.SubCommand;
import org.bukkit.entity.Player;

@Command(names = {"drapuriatest"}, description = "Drapuria Test Command")
public class TestCommand {

    @SubCommand(names = "arguments", parameters = "{Integer} {Text}")
    public void argsTestWithInteger(final DrapuriaPlayer player, final Integer integer, @CommandParameter(wildcard = true) final String text) {
        player.sendMessage("used {Integer} {Text}");
        player.sendMessage("Output: " + integer + " " + text);
    }

    @SubCommand(names = "arguments", parameters = "{Text}")
    public void argsTestNoInteger(final DrapuriaPlayer player, @CommandParameter(wildcard = true) final String text) {
        player.sendMessage("used {Text}");
        player.sendMessage("Output: " + text);
    }

    @SubCommand(names = "bbb", parameters = "{Integer}")
    public void bbb(final DrapuriaPlayer player, Integer integer) {
        player.sendMessage("used {Integer}");
    }

    @SubCommand(names = "bbb", parameters = "{Player}")
    public void bbb(final DrapuriaPlayer player, Player target) {
        player.sendMessage("used {Player}");
    }

}
