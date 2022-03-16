/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.location;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

@Component
public class WorldSerializer implements ObjectSerializer<World, String> {
    @Override
    public String serialize(World input) {
        return input.getUID().toString();
    }

    @Override
    public World deserialize(String output) {
        return Bukkit.getWorld(UUID.fromString(output));
    }

    @Override
    public Class<World> inputClass() {
        return World.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}
