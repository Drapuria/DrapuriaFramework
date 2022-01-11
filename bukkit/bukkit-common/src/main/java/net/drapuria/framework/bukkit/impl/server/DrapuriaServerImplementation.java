package net.drapuria.framework.bukkit.impl.server;

import net.drapuria.framework.bukkit.impl.annotation.ServerImpl;
import net.drapuria.framework.bukkit.util.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ServerImpl
public class DrapuriaServerImplementation implements ServerImplementation{
    @Override
    public Entity getEntity(UUID uuid) {
        return null;
    }

    @Override
    public Entity getEntity(World world, int id) {
        return null;
    }

    @Override
    public Object toBlockNMS(MaterialData materialData) {
        return null;
    }

    @Override
    public List<Player> getPlayersInRadius(Location location, double radius) {
        return null;
    }

    @Override
    public void setFakeBlocks(Player player, Map<BlockPosition, MaterialData> positions, List<BlockPosition> toRemove, boolean send) {

    }

    @Override
    public void clearFakeBlocks(Player player, boolean send) {

    }

    @Override
    public void sendActionBar(Player player, String message) {

    }

    @Override
    public boolean isServerThread() {
        return false;
    }

    @Override
    public boolean callMoveEvent(Player player, Location from, Location to) {
        return false;
    }
}
