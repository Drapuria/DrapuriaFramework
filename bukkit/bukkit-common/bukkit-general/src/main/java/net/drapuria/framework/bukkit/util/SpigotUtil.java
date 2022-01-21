/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util;

public class SpigotUtil {

    public static SpigotType SPIGOT_TYPE;

    public static void init() {
        try {
            Class.forName("de.vantrex.hardcorespigot.others.config.HardcoreSpigotConfig");
            SPIGOT_TYPE = SpigotType.HARDCORE_SPIGOT;
            return;
        } catch (ClassNotFoundException ignored) { }

        try {
            Class.forName("org.github.paperspigot.PaperSpigotConfig");
            SPIGOT_TYPE = SpigotType.PAPER;
            return;
        } catch (ClassNotFoundException ignored) {}

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            SPIGOT_TYPE = SpigotType.PAPER;
            return;
        } catch (ClassNotFoundException ignored) {}

        try {
            Class.forName("org.spigotmc.SpigotConfig");
            SPIGOT_TYPE = SpigotType.SPIGOT;
            return;
        } catch (ClassNotFoundException ignored) {}
        SPIGOT_TYPE = SpigotType.CRAFTBUKKIT;
    }

    public enum SpigotType {
        HARDCORE_SPIGOT,
        PAPER,
        SPIGOT,
        CRAFTBUKKIT
    }

}
