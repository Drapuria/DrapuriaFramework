/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.scoreboard.SidebarOptions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class DrapuriaBoard {

    public static int MAX_PREFIX_SUFFIX_LENGTH = 16;

    protected final Player player;
    protected final SidebarOptions options;

    @Setter
    protected String title;
    private final String[] lines;

    public DrapuriaBoard(SidebarOptions options, Player player, String title) {
        this.options = options;
        this.player = player;
        this.title = title;
        this.lines = new String[16];
        if (title != null)
            createBoard();
        this.title = "drapuria";
    }

    public void setLines(List<String> lines) {
        int currentLine = 1;
        for (int i = lines.size() - 1; i >= 0; --i) {
            setLine(currentLine, lines.get(i));
            currentLine++;
        }
        for (int i = lines.size(); i < 15; i++) {
            if (this.lines[currentLine] != null)
                this.clear(currentLine);
            currentLine++;
        }
    }

    public void setLine(int line, String text) {
        if (line <= 0 || line >= 16)
            return;
        if (lines[line] != null && lines[line].equals(text))
            return;

        String prefix, suffix;
        if (text.length() > MAX_PREFIX_SUFFIX_LENGTH) {
            String first = text.substring(0, MAX_PREFIX_SUFFIX_LENGTH);
            String second = text.substring(MAX_PREFIX_SUFFIX_LENGTH);
            if (first.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                first = first.substring(0, first.length() - 1);
                second = ChatColor.COLOR_CHAR + second;
            }
            String lastColors = ChatColor.getLastColors(first);
            second = lastColors + second;
            prefix = first;
            suffix = StringUtils.left(second, MAX_PREFIX_SUFFIX_LENGTH);
        } else {
            suffix = "";
            prefix = text;
        }
        final String entry = getEntry(line);
        sendLine(line, entry, entry, prefix, suffix);
        lines[line] = text;
    }

    public void clear(int line) {
        if (line >= 0 && line <= 16 && lines[line] != null) {
            sendClear(line, getEntry(line));
            lines[line] = null;
        }
        if (Arrays.stream(lines).allMatch(Objects::isNull)) {
            sendDestroy();
        }
    }

    public void remove() {
        for (int i = 1; i < 15; i++) {
            clear(i);
        }
        sendDestroy();
    }

    private final static String[] nextEntries = {"a", "b", "c", "d", "e", "f"};

    public static String getEntry(Integer line) {
        if (line > 0 && line <= 16)
            if (line <= 10)
                return ChatColor.COLOR_CHAR + "" + (line - 1) + ChatColor.WHITE;
            else {
                return ChatColor.COLOR_CHAR + nextEntries[line - 11] + ChatColor.WHITE;
            }
        return "";
    }


    public abstract void createBoard();

    public abstract void sendLine(int line, String team, String entry, String prefix, String suffix);

    public abstract void sendClear(int line, String entry);

    public abstract void sendDestroy();

}