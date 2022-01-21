/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class DrapuriaBoard {

    protected final Player player;

    @Setter
    private String title;
    private final String[] lines;

    public DrapuriaBoard(Player player, String title) {
        this.player = player;
        this.title = title;
        this.lines = new String[16];
        // TODO SEND CREATE PACKET
    }

    public void setLines(List<String> lines) {
        int currentLine = 0;
        for (int i = lines.size() - 1; i >= 0; i--) {
            setLine(currentLine, lines.get(i));
            currentLine++;
        }
        for (int i = 0; i < lines.size(); i++) {
            if (this.lines[i] != null)
                this.clear(currentLine);
            currentLine++;
        }
    }

    public void setLine(int line, String text) {
        if (lines[line] != null && lines[line].equals(text))
            return;

        String prefix, suffix;
        if (text.length() > 16) {
            String first = text.substring(0, 16);
            String second = text.substring(16);
            if (first.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                first = first.substring(0, first.length() - 1);
                second = ChatColor.COLOR_CHAR + second;
            }
            String lastColors = ChatColor.getLastColors(first);
            second = lastColors + second;
            prefix = first;
            suffix = StringUtils.left(second, 16);
        } else {
            suffix = "";
            prefix = "";
        }
        // TODO SEND PACKET
    }

    public void clear(int line) {
        lines[line] = null;
        // TODO SEND REMOVE PACKET
    }

    public abstract void createBoard();

    public abstract void sendLine(int line, String team, String entry, String prefix, String suffix);

    public abstract void sendClear(int line);

    public abstract void sendDestroy();

}