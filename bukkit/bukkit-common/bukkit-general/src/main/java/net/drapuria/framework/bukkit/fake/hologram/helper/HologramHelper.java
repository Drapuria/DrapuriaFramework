package net.drapuria.framework.bukkit.fake.hologram.helper;

import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.Location;

@UtilityClass
public class HologramHelper {

    public boolean isInRange(final Location playerLocation, final Location hologramLocation) {
        if (!playerLocation.getWorld().equals(hologramLocation.getWorld())) return false;
        final Chunk playerChunk = playerLocation.getChunk(), hologramChunk = hologramLocation.getChunk();
        return diff(playerChunk.getX(), hologramChunk.getX()) <= 4 && diff(playerChunk.getZ(), hologramChunk.getZ()) <= 4;
    }

    private int diff(int x1, int x2) {
        return Math.abs(x1 - x2);
    }
}