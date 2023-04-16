package net.drapuria.framework.bukkit.protocol.packet.wrapper.server.entity;

import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import org.bukkit.entity.Entity;

@AutowiredWrappedPacket(value = PacketType.Server.REL_ENTITY_MOVE_LOOK, direction = PacketDirection.WRITE)
@Getter
public class WrappedPacketOutRelEntityMoveLook extends WrappedPacket {
    private int entityID;
    private Entity entity;
    private double deltaX, deltaY, deltaZ;
    private byte yaw, pitch;
    private boolean onGround;

    public WrappedPacketOutRelEntityMoveLook(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        entityID = readInt(0);
        onGround = readBoolean(0);
        int dX = 1, dY = 1, dZ = 1;
        switch (EntityPacketUtil.getMode()) {
            case 0:
                dX = readByte(0);
                dY = readByte(1);
                dZ = readByte(2);
                yaw = readByte(3);
                pitch = readByte(4);
                break;
            case 1:
                dX = readInt(1);
                dY = readInt(2);
                dZ = readInt(3);
                yaw = readByte(0);
                pitch = readByte(1);
                break;
            case 2:
                dX = readShort(0);
                dY = readShort(1);
                dZ = readShort(2);
                yaw = readByte(0);
                pitch = readByte(1);
                break;
        }
        deltaX = dX / EntityPacketUtil.getDXYZDivisor();
        deltaY = dY / EntityPacketUtil.getDXYZDivisor();
        deltaZ = dZ / EntityPacketUtil.getDXYZDivisor();
    }

    /**
     * Lookup the associated entity by the ID that was sent in the packet.
     *
     * @return Entity
     */
    public Entity getEntity() {
        if (entity != null) {
            return entity;
        }
        return entity = Drapuria.IMPLEMENTATION.getEntity(this.entityID);
    }
}