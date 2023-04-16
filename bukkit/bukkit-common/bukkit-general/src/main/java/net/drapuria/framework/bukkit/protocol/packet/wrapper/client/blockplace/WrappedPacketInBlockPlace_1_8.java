package net.drapuria.framework.bukkit.protocol.packet.wrapper.client.blockplace;

import lombok.Getter;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ObjectWrapper;
import net.drapuria.framework.bukkit.util.BlockPosition;
import org.bukkit.inventory.ItemStack;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;

@Getter
final class WrappedPacketInBlockPlace_1_8 extends WrappedPacket {
    private static Class<?>
            BLOCK_POSITION,
            BLOCK_POSITION_SUPER;

    private BlockPosition blockPosition;
    private ItemStack itemStack;

    WrappedPacketInBlockPlace_1_8(final Object packet) {
        super(packet);
    }

    protected static void load() {
        try {
            BLOCK_POSITION = NMS_CLASS_RESOLVER.resolve("BlockPosition");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        BLOCK_POSITION_SUPER = BLOCK_POSITION.getSuperclass();
    }

    @Override
    protected void setup() {
        Object nmsBlockPos = packet.getPacketValueByIndex(BLOCK_POSITION, 0);

        this.blockPosition = new BlockPosition(0, 0, 0, this.getWorld().getName());

        ObjectWrapper objectWrapper = new ObjectWrapper(nmsBlockPos);

        this.blockPosition.setX(objectWrapper.invoke("getX"));
        this.blockPosition.setY(objectWrapper.invoke("getY"));
        this.blockPosition.setZ(objectWrapper.invoke("getZ"));

        this.itemStack = this.readItemStack(0);
    }
}
