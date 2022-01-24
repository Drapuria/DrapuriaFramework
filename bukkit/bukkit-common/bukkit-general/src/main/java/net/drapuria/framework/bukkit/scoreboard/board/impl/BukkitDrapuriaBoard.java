/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board.impl;

import net.drapuria.framework.bukkit.scoreboard.SidebarOptions;
import net.drapuria.framework.bukkit.scoreboard.board.DrapuriaBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class BukkitDrapuriaBoard extends DrapuriaBoard {

    private Scoreboard scoreboard;
    private Objective sidebar;

    public BukkitDrapuriaBoard(SidebarOptions options, Player player, String title) {
        super(options, player, title);
    }

    @Override
    public void createBoard() {
        if (options.hook() && player.getScoreboard() != null) {
            scoreboard = player.getScoreboard();
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        sidebar = scoreboard.registerNewObjective("aaa", "bbb");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.setDisplayName(this.getTitle());
    }

    @Override
    public void sendLine(int line, String team, String entry, String prefix, String suffix) {
        if (sidebar == null || scoreboard == null)
            createBoard();
        Team scoreboardTeam = scoreboard.getTeam(team);
        if (scoreboardTeam == null)
            scoreboardTeam = scoreboard.registerNewTeam(team);
        if (!scoreboardTeam.hasEntry(entry))
            scoreboardTeam.addEntry(entry);
        scoreboardTeam.setPrefix(prefix);
        scoreboardTeam.setSuffix(suffix);
        sidebar.getScore(team).setScore(line);
    }

    @Override
    public void sendClear(int line, String entry) {
        final Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard != null) {
            Team team = scoreboard.getEntryTeam(entry);
            if (team != null) {
                team.removeEntry(entry);
                team.unregister();
            }

        }
    }

    @Override
    public void setTitle(String title) {
        boolean changed = !getTitle().equals(title);
        super.setTitle(title);
        if (changed)
            this.scoreboard.getObjective(DisplaySlot.SIDEBAR)
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
    }

    @Override
    public void sendDestroy() {
        final Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard != null) {
            scoreboard.getObjective(DisplaySlot.SIDEBAR).unregister();
            scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}