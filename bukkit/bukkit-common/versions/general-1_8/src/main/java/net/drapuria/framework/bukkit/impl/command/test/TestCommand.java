/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.test;

import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.Menu;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.DefaultCommand;
import net.drapuria.framework.command.annotation.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

//@Command(names = {"drapuriatest"}, description = "Drapuria Test Command")
public class TestCommand extends DrapuriaCommand {

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
