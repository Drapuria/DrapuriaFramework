/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.commands;

import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.scoreboard.ScoreboardService;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.Executor;
import net.drapuria.framework.command.annotation.SubCommand;
import org.bukkit.entity.Player;

@Command(names = {"drapuria"})
public class FrameworkCommand {

    private final ScoreboardService testService;

    public FrameworkCommand(ScoreboardService testService) {
        this.testService = testService;
    }

    @Executor
    public void execute(final DrapuriaPlayer player) {
        player.sendActionBar("§a§lWORKING");
        //   player.sendActionBar("§fThis ");
    }
    @Executor(parameters = "Spieler")
    public void execute(final DrapuriaPlayer player, final DrapuriaPlayer target) {
        player.sendMessage("-> " + target);
    }

    @SubCommand(names = "test", parameters = "{Spieler}")
    public void test(final DrapuriaPlayer player, final Player target) {
        player.sendMessage("TARGET: " + target);
    }
}
