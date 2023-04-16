package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@AutowiredWrappedPacket(value = PacketType.Client.HELD_ITEM_SLOT, direction = PacketDirection.READ)
@Getter
public final class WrappedPacketInHeldItemSlot extends WrappedPacket {

    private int itemInHandIndex;

    public WrappedPacketInHeldItemSlot(Object packet) {
        super(packet);
    }

    @Override
    public void setup() {
        try {
            itemInHandIndex = readInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
