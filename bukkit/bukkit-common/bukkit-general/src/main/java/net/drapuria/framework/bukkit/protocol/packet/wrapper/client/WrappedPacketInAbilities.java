package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;

@Getter
@AutowiredWrappedPacket(value = PacketType.Client.ABILITIES, direction = PacketDirection.READ)
public final class WrappedPacketInAbilities extends WrappedPacket {

    private static final boolean MULTIPLE_ABILITIES;

    static {
        MULTIPLE_ABILITIES = new FieldResolver(PacketTypeClasses.Client.ABILITIES)
                .resolveSilent(boolean.class, 1)
                .get(null);
    }

    private boolean vulnerable;
    private boolean flying;
    private boolean allowFly;
    private boolean instantBuild;
    private float flySpeed;
    private float walkSpeed;

    public WrappedPacketInAbilities(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {
        if (MULTIPLE_ABILITIES) {
            this.vulnerable = readBoolean(0);
            this.flying = readBoolean(1);
            this.allowFly = readBoolean(2);
            this.instantBuild = readBoolean(3);
            this.flySpeed = readFloat(0);
            this.walkSpeed = readFloat(1);
        } else {
            this.flying = readBoolean(0);
        }
    }

    public void setVulnerable(boolean vulnerable) {
        this.validBasePacketExists();

        this.packet.setFieldByIndex(boolean.class, 0, vulnerable);
        this.vulnerable = vulnerable;
    }
}
