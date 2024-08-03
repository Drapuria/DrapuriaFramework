package net.drapuria.framework.bukkit.fake.hologram.line;

import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramOffsets;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ConsumedTextLine implements Line {

    private final int entityId;
    @Getter
    @Setter
    private Function<Player, String> function;
    private Map<Player, String> cache = new HashMap<>();
    public ConsumedTextLine(int entityId, Function<Player, String> function) {
        this.entityId = entityId;
        this.function = function;
    }

    @Override
    public double getHeight() {
        return .23D;
    }

    @Override
    public PacketContainer[] getSpawnPackets(Player player, double x, double y, double z) {
        final double offset = HologramOffsets.getOffset(player);
        final String text = function.apply(player);
        cache.put(player, text);
        return new PacketContainer[]{PacketHelper.armorStandSpawn(this.entityId, x, y + offset - 1.25D, z),
                PacketHelper.armorStandMeta(this.entityId,
                        text)};
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
        final String text = function.apply(player);
        if (cache.containsKey(player) && cache.get(player).equals(text)) {
            return new PacketContainer[0];
        }
        cache.put(player, text);
        return new PacketContainer[]{PacketHelper.armorStandMeta(this.entityId, text)};
    }
}