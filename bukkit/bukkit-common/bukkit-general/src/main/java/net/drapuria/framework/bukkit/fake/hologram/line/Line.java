package net.drapuria.framework.bukkit.fake.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public interface Line {

    double getHeight();

    PacketContainer[] getSpawnPackets(final Player player, double x, double y, double z);

    PacketContainer[] getDestroyPackets();

    PacketContainer[] getTeleportPackets(Player player, double oldX, double oldY, double oldZ, double newX, double newY, double newZ);

    PacketContainer[] getUpdatePackets(Player player);

}
