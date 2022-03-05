/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board.adapter.example;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.scoreboard.board.adapter.DefaultAdapter;
import net.drapuria.framework.bukkit.scoreboard.board.adapter.ScoreboardAdapter;
import net.drapuria.framework.bukkit.text.SimpleAnimatedText;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

//@Component
public class TestAdapter implements ScoreboardAdapter, DefaultAdapter {

    /**
     * The animation should not go to the next @ getTitle should be in a seperated scheduler
     */

    private final SimpleAnimatedText text = new SimpleAnimatedText("Drapuria", "§8§l", "§5§l", "§d§l", "§5§l");
    private final SimpleAnimatedText text2 = new SimpleAnimatedText("DAS IST EIN SEHR SEHR SEHR SEHR LANGER TEXT", "§7§l", "§a§l", "§2§l", "§a§l");
    private boolean b = false;
    private int i = 4;
    @Override
    public String getTitle(Player player) {
        return text.next();
    }

    @Override
    public List<String> getLines(Player player) {

        List<String> lines = new ArrayList<>();
        lines.add(text2.last());
        /*
        if (b) {
            i++;
            if (i == 16)
                b = false;
        } else {
            i--;
            if (i == 0)
                b = true;
        }
        if (i == 0)
            return new ArrayList<>();
        lines = IntStream.range(0, i).mapToObj(String::valueOf).collect(Collectors.toList());
         */
        return lines;
    }

    @Override
    public long getTickTime() {
        return 3L;
    }
}