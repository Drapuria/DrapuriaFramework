/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.location;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.Location;

@Component
public class LocationSerializer implements ObjectSerializer<Location, String> {
    @Override
    public String serialize(Location input) {
        return BukkitUtil.locationToString(input, true);
    }

    @Override
    public Location deserialize(String output) {
        return BukkitUtil.stringToLocation(output, true);
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