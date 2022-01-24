/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board.adapter;

import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardAdapter {

    String getTitle(final Player player);

    List<String> getLines(final Player player);

    long getTickTime();

}
