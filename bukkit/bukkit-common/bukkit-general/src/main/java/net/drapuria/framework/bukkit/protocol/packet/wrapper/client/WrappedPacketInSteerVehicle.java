package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@AutowiredWrappedPacket(value = PacketType.Client.STEER_VEHICLE, direction = PacketDirection.READ)
@Getter
public class WrappedPacketInSteerVehicle extends WrappedPacket {

    private float side, forward;
    private boolean jump, unmount;

    public WrappedPacketInSteerVehicle(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        this.side = readFloat(0);
        this.forward = readFloat(1);

        this.jump = readBoolean(0);
        this.unmount = readBoolean(1);
    }
}
