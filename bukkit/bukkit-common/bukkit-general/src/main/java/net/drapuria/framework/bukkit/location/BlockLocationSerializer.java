/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.location;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.Location;

public class BlockLocationSerializer implements ObjectSerializer<Location, String> {
    @Override
    public String serialize(Location input) {
        return BukkitUtil.locationToString(input, false);
    }

    @Override
    public Location deserialize(String output) {
        return BukkitUtil.stringToLocation(output, false);
    }

    @Override
    public Class<Location> inputClass() {
        return Location.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}
