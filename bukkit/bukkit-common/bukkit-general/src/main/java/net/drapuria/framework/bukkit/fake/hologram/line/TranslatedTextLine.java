package net.drapuria.framework.bukkit.fake.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramOffsets;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import org.bukkit.entity.Player;

/**
 * This line will automatically get translated (WIP)
 */
public class TranslatedTextLine implements Line {

    private final int entityId;
    @Getter
    @Setter
    private String textToTranslate;

    public TranslatedTextLine(int entityId, String textToTranslate) {
        this.entityId = entityId;
        this.textToTranslate = textToTranslate;
    }

    @Override
    public double getHeight() {
        return .23D;
    }

    @Override
    public PacketContainer[] getSpawnPackets(Player player, double x, double y, double z) {
        final double offset = HologramOffsets.getOffset(player);
        return new PacketContainer[]{PacketHelper.armorStandSpawn(this.entityId, x, y + offset - 1.25D, z), PacketHelper.armorStandMeta(this.entityId, this.textToTranslate)};
    }

    @Override
    public PacketContainer[] getDestroyPackets() {
        return new PacketContainer[]{PacketHelper.entityDestroy(this.entityId)};
    }

    @Override
    public PacketContainer[] getTeleportPackets(Player player, double oldX, double oldY, double oldZ, double newX, double newY, double newZ) {
        double offset = HologramOffsets.getOffset(player);
        newY += offset;
        return new PacketContainer[]{PacketHelper.entityTeleport(this.entityId, newX, newY - 1.25D, newZ)};
    }

    @Override
    public PacketContainer[] getUpdatePackets(Player player) {
        return new PacketContainer[]{PacketHelper.armorStandMeta(this.entityId, this.textToTranslate)};
    }
}
