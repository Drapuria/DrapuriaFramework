package net.drapuria.framework.bukkit.protocol.packet.wrapper.client.blockplace;

import lombok.Getter;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.util.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@Getter
@AutowiredWrappedPacket(value = PacketType.Client.BLOCK_PLACE, direction = PacketDirection.READ)
public final class WrappedPacketInBlockPlace extends WrappedPacket {

    private BlockPosition blockPosition;
    private ItemStack itemStack;

    public WrappedPacketInBlockPlace(final Player player, final Object packet) {
        super(player, packet);
    }

    public static void load() {
        if (MinecraftVersion.VERSION.newerThan(Minecraft.Version.v1_7_R1)) {
            WrappedPacketInBlockPlace_1_8.load();
        }
    }

    @Override
    protected void setup() {
        //1.7.10
        BlockPosition position = null;
        ItemStack itemStack = null;
        try {

            if (MinecraftVersion.VERSION.newerThan(Minecraft.Version.v1_8_R4)) {

                final WrappedPacketInBlockPlace_1_9 blockPlace_1_9 = new WrappedPacketInBlockPlace_1_9(getPlayer(), packet);
                final Block block = blockPlace_1_9.getBlock();
                position = new BlockPosition(block.getX(), block.getY(), block.getZ(), this.getWorld().getName());
                itemStack = new ItemStack(block.getType());

            } else  {
                final WrappedPacketInBlockPlace_1_8 blockPlace_1_8 = new WrappedPacketInBlockPlace_1_8(packet);
                position = blockPlace_1_8.getBlockPosition();
                itemStack = blockPlace_1_8.getItemStack();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        this.blockPosition = position;
        this.itemStack = itemStack;
    }
}