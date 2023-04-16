package net.drapuria.framework.bukkit.protocol.packet.wrapper.server.entity;

import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import org.bukkit.entity.Entity;


@AutowiredWrappedPacket(value = PacketType.Server.ENTITY, direction = PacketDirection.WRITE)
@Getter
public class WrappedPacketOutEntity extends WrappedPacket {

    private Entity entity;
    private int entityID;
    private double deltaX, deltaY, deltaZ;
    private byte yaw, pitch;
    private boolean onGround;

    public WrappedPacketOutEntity(Object packet) {
        super(packet);
    }

    public static void init() {
        EntityPacketUtil.init();
    }

    @Override
    protected void setup() {
        this.entityID = readInt(0);
        this.onGround = readBoolean(0);
        int dX = 1, dY = 1, dZ = 1;
        switch (EntityPacketUtil.getMode()) {
            case 0:
                dX = readByte(0);
                dY = readByte(1);
                dZ = readByte(2);
                this.yaw = readByte(3);
                this.pitch = readByte(4);
                break;
            case 1:
                dX = readInt(1);
                dY = readInt(2);
                dZ = readInt(3);
                this.yaw = readByte(0);
                this.pitch = readByte(1);
                break;
            case 2:
                dX = readShort(0);
                dY = readShort(1);
                dZ = readShort(2);
                this.yaw = readByte(0);
                this.pitch = readByte(1);
                break;
        }
        this.deltaX = dX / EntityPacketUtil.getDXYZDivisor();
        this.deltaY = dY / EntityPacketUtil.getDXYZDivisor();
        this.deltaZ = dZ / EntityPacketUtil.getDXYZDivisor();
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
