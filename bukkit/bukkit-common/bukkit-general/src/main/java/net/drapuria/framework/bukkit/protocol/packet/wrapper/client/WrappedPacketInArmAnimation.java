package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@AutowiredWrappedPacket(value = PacketType.Client.ARM_ANIMATION, direction = PacketDirection.READ)
public final class WrappedPacketInArmAnimation extends WrappedPacket {

    private long timestamp;

    public WrappedPacketInArmAnimation(Object packet) {
        super(packet);
    }

    @Override
    public void setup() {
        this.timestamp = readLong(0);
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
