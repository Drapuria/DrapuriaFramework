package net.drapuria.framework.bukkit.fake.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

public class EmptyLine implements Line {


    private final double height;

    public EmptyLine(double height) {
        this.height = height;
    }

    public EmptyLine() {
        this.height = 0.23D;
    }

    @Override
    public double getHeight() {
        return this.height;
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
