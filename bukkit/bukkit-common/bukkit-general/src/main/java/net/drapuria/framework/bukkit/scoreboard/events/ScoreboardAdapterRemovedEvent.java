/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.drapuria.framework.bukkit.events.BaseEvent;
import net.drapuria.framework.bukkit.scoreboard.board.adapter.ScoreboardAdapter;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public class ScoreboardAdapterRemovedEvent extends BaseEvent {

    private final Player player;
    private final ScoreboardAdapter removedAdapter;
    private ScoreboardAdapter newAdapter = null;

}
