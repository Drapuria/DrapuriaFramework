/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util;

import lombok.experimental.UtilityClass;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.BlockIterator;

/**
 * A class containing various utils helping with spigot development.
 */
@UtilityClass
public class BukkitUtil {

    /**
     * Converts a {@link Location} to a {@link String}.
     *
     * @param loc      Bukkit location
     * @param yawPitch Convert the yaw and pit as well?
     * @return Location as String
     **/
    public String locationToString(Location loc, boolean yawPitch) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ()
                + (yawPitch ? (";" + loc.getYaw() + ";" + loc.getPitch()) : "");
    }

    /**
     * Converts a {@link String} to a {@link Location}.
     *
     * @param stringLoc Location as String
     * @param yawPitch  Convert the yaw and pitch as well?
     * @return The Bukkit Location
     */
    public Location stringToLocation(String stringLoc, boolean yawPitch) {
        final String[] splitted = stringLoc.split(";");
        final Location loc = new Location(Bukkit.getWorld(splitted[0]),
                Double.parseDouble(splitted[1]),
                Double.parseDouble(splitted[2]),
                Double.parseDouble(splitted[3]));
        if (yawPitch && splitted.length > 4) {
            loc.setYaw(Float.parseFloat(splitted[4]));
            loc.setPitch(Float.parseFloat(splitted[5]));
        }
        return loc;
    }

    /**
     * Gets the squared {@link Double} distance of 2 locations in a 3 dimensional coordinate system (e.G. Minecraft).
     *
     * @param pointX1 Location#getX
     * @param pointX2 Location2#getX
     * @param pointY1 Location1#getY
     * @param pointY2 Location2#getY
     * @param pointZ1 Location1#getY
     * @param pointZ2 Location2#getY
     *
     * @return The squared distance between all points
     */
    public double distanceSquared(final double pointX1, final double pointX2, final double pointY1,
                                  final double pointY2, final double pointZ1, final double pointZ2) {
        final double x = pointX1 - pointX2;
        final double y = pointY1 - pointY2;
        final double z = pointZ1 - pointZ2;
        return x * x + y * y + z * z;
    }

    /**
     * Checks if Event class is a player event
     *
     * @param event The {@link Class event} of the event
     * @return true if it is a player event false if not
     */
    public boolean isPlayerEvent(Class<?> event) {

        if (PlayerEvent.class.isAssignableFrom(event)) {
            return true;
        }
        MethodResolver resolver = new MethodResolver(event);
        return resolver.resolveWrapper("getPlayer").exists();
    }

    public boolean locationEquals(final Location location1, final Location location2) {
        return location1 != null && location2 != null && location2.getWorld().equals(location2.getWorld()) && location1.getX() == location2.getX() && location1.getY() == location2.getY() && location1.getZ() == location2.getZ();
    }

    public Block getBlockLookingAt(final Player player, final int distance) {
        final Location location = player.getEyeLocation();
        final BlockIterator blocksToAdd = new BlockIterator(location, 0.0D, distance);
        Block block = null;
        while (blocksToAdd.hasNext()) {
            block = blocksToAdd.next();
        }
        return block;
    }

    public boolean isNPC(Player player) {
        if (player.hasMetadata("DrapuriaBot")) {
            return true;
        }

        if (player.hasMetadata("NPC")) {
            return true;
        }
        return false;
    }

}
