package net.drapuria.framework.bukkit.fake.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramOffsets;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Setter
public class ItemLine implements Line {

    private final int entityId;
    private final int armorStandId;
    @Getter
    private ItemStack item;

    public ItemLine(int entityId, int armorStandId, ItemStack item) {
        this.entityId = entityId;
        this.armorStandId = armorStandId;
        this.item = item;
    }

    @Override
    public double getHeight() {
        return .7D;
    }

    @Override
    public PacketContainer[] getSpawnPackets(Player player, double x, double y, double z) {
        final double offset = HologramOffsets.getOffset(player);
        y += offset;
        return new PacketContainer[]{
                PacketHelper.itemSpawn(this.entityId, x, y, z),
                PacketHelper.itemMeta(this.entityId, this.item),
                PacketHelper.armorStandSpawn(this.armorStandId, x, y - 1.48D, z),
                PacketHelper.itemArmorStandMeta(this.armorStandId),
                PacketHelper.itemArmorStandAttach(this.entityId, this.armorStandId)
        };
    }

    @Override
    public PacketContainer[] getDestroyPackets() {
        return new PacketContainer[]{
                PacketHelper.entityDestroy(this.entityId)
        };
    }

    @Override
    public PacketContainer[] getTeleportPackets(Player player, double oldX, double oldY, double oldZ, double newX, double newY, double newZ) {
        final double offset = HologramOffsets.getOffset(player);
        newY += offset;
        return new PacketContainer[]{
                PacketHelper.entityTeleport(this.armorStandId, newX, newY - 1.48D, newZ)
        };
    }

    @Override
    public PacketContainer[] getUpdatePackets(Player player) {
        return new PacketContainer[]{
                PacketHelper.itemMeta(this.entityId, this.item)
        };
    }
}
