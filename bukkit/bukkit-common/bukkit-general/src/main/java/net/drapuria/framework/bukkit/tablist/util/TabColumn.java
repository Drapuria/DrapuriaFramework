package net.drapuria.framework.bukkit.tablist.util;

import lombok.Getter;
import net.drapuria.framework.bukkit.reflection.version.PlayerVersion;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum TabColumn {

    LEFT(0, "Left", -2, 1, 3),
    MIDDLE(1, "Middle", -1, 21, 3),
    RIGHT(2, "Right", 0, 41, 3),
    /**
     * Far Right Column
     * Note: This Column is only visible on 1.8+ player versions.
     */
    FAR_RIGHT(3, "Far-Right", 60, 61, 1);

    private int startNumber;
    private int incrementBy;
    private int rawStart;
    private List<Integer> numbers = new ArrayList<>();
    private String identifier;
    private int ordinal;

    TabColumn(int ordinal, String identifier, int rawStart, int startNumber, int incrementBy) {
        this.ordinal = ordinal;
        this.identifier = identifier;
        this.rawStart = rawStart;
        this.startNumber = startNumber;
        this.incrementBy = incrementBy;
        generate();
    }

    public static TabColumn getColumn(String identifier) {
        for (TabColumn tabColumn : TabColumn.values()) {
            if (tabColumn.getIdentifier().equalsIgnoreCase(identifier)) {
                return tabColumn;
            }
        }
        return null;
    }

    private void generate() {
        for (int i = 1; i <= 20; i++) {
            Integer numb = rawStart + (i * incrementBy);
            this.numbers.add(numb);
        }
    }

    public static TabColumn getFromSlot(Player player, Integer slot) {
        /* Left Column */
        if (isBetween(slot, 1, 20)) return LEFT;
        /* Middle Column */
        if (isBetween(slot, 21, 40)) return MIDDLE;
        /* Right Column */
        if (isBetween(slot, 41, 60)) return RIGHT;
        /* Far Right Column */
        if (isBetween(slot, 61, 80)) return FAR_RIGHT;
        return null;
    }

    public Integer getNumb(Player player, int raw) {
        /* Check if the Player is not a 1.7 User */
        if (PlayerUtil.getVersion(player) != PlayerVersion.v1_7) {
            return raw - startNumber + 1;
        }
        int number = 0;
        for (int integer : numbers) {
            number++;
            if (integer == raw) {
                return number;
            }
        }
        return number;
    }

    public static TabColumn getFromOrdinal(int ordinal) {
        for (TabColumn column : TabColumn.values()) {
            if (column.getOrdinal() == ordinal) {
                return column;
            }
        }
        return null;
    }

    private static boolean isBetween(int base, int from, int to) {
        return from <= base && base <= to;
    }

}
