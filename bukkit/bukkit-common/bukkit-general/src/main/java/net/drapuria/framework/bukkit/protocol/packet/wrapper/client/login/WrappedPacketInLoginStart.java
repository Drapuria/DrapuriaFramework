package net.drapuria.framework.bukkit.protocol.packet.wrapper.client.login;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.GameProfileWrapper;

@AutowiredWrappedPacket(value = PacketType.Client.LOGIN_START, direction = PacketDirection.READ)
@Getter
public class WrappedPacketInLoginStart extends WrappedPacket {

    private GameProfileWrapper gameProfile;

    public WrappedPacketInLoginStart(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        this.gameProfile = this.readGameProfile(0);
    }

}
