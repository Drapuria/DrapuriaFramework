package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@Getter
@AutowiredWrappedPacket(value = PacketType.Client.CHAT, direction = PacketDirection.READ)
public final class WrappedPacketInChat extends WrappedPacket {

    private String message;

    public WrappedPacketInChat(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        this.message = readString(0);
    }

}
