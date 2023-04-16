package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;


@AutowiredWrappedPacket(value = PacketType.Client.TRANSACTION, direction = PacketDirection.READ)
@Getter
public final class WrappedPacketInTransaction extends WrappedPacket {

    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public WrappedPacketInTransaction(final Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        this.windowId = readInt(0);
        this.actionNumber = readShort(0);
        this.accepted = readBoolean(0);
    }

}
