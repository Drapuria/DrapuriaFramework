/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.world;

import net.drapuria.framework.beans.annotation.PreInitialize;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

//@Service(name = "worldHandler")
public class WorldHandler {

    public static WorldHandler getHandler;

    private static final Map<String, World> loadedWorlds = new HashMap<>();


    @PreInitialize
    public void load() {
        getHandler = this;
    }

}
