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
public class WorldSerializer implements ObjectSerializer<World, UUID> {
    @Override
    public UUID serialize(World input) {
        return input.getUID();
    }

    @Override
    public World deserialize(UUID output) {
        return Bukkit.getWorld(output);
    }

    @Override
    public Class<World> inputClass() {
        return World.class;
    }

    @Override
    public Class<UUID> outputClass() {
        return UUID.class;
    }
}
