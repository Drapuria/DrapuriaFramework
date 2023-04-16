package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@AutowiredWrappedPacket(value = PacketType.Client.FLYING, direction = PacketDirection.READ)
@Getter
public class WrappedPacketInFlying extends WrappedPacket {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private boolean position;
    private boolean look;

    public WrappedPacketInFlying(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        x = readDouble(0);
        y = readDouble(1);
        z = readDouble(2);

        yaw = readFloat(0);
        pitch = readFloat(1);

        onGround = readBoolean(0);

        position = readBoolean(1);
        look = readBoolean(2);
    }

}
