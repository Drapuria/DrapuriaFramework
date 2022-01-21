/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board.impl;

import net.drapuria.framework.bukkit.scoreboard.ScoreboardOptions;
import net.drapuria.framework.bukkit.scoreboard.board.DrapuriaBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;

public class BukkitDrapuriaBoard extends DrapuriaBoard {

    private final ScoreboardOptions options;
    private Scoreboard scoreboard;
    private Objective sidebar;

    public BukkitDrapuriaBoard(ScoreboardOptions options, Player player, String title) {
        super(player, title);
        this.options = options;
    }

    @Override
    public void createBoard() {
        if (options.hook()) {
            scoreboard = player.getScoreboard();
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        sidebar = scoreboard.registerNewObjective("aaa", "bbb");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void sendLine(int line, String team, String entry, String prefix, String suffix) {
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
    public void sendClear(int line) {

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

    }
}
