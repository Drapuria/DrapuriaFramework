/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.world;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.impl.configuration.BukkitYamlConfiguration;

import java.io.File;

public class WorldConfig extends BukkitYamlConfiguration {

    public WorldConfig() {
        super(new File(DrapuriaCommon.PLATFORM.getDataFolder(), "worlds.yml").toPath());
        loadAndSave();
    }
}
