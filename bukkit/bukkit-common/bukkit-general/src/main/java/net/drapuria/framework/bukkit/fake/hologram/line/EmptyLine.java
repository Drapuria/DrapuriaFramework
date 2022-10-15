package net.drapuria.framework.bukkit.fake.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class EmptyLine implements Line {
    @Override
    public double getHeight() {
        return .23D;
    }

    @Override
    public PacketContainer[] getSpawnPackets(Player player, double x, double y, double z) {
        return new PacketContainer[0];
    }

    @Override
    public PacketContainer[] getDestroyPackets() {
        return new PacketContainer[0];
    }

    @Override
    public PacketContainer[] getTeleportPackets(Player player, double oldX, double oldY, double oldZ, double newX, double newY, double newZ) {
        return new PacketContainer[0];
    }

    @Override
    public PacketContainer[] getUpdatePackets(Player player) {
        return new PacketContainer[0];
    }
}
