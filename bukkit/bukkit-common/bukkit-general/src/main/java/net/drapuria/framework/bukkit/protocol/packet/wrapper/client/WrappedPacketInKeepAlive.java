package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;


@AutowiredWrappedPacket(value = PacketType.Client.KEEP_ALIVE, direction = PacketDirection.READ)
@Getter
public final class WrappedPacketInKeepAlive extends WrappedPacket {
    private static boolean integerPresent;
    private long id;

    public WrappedPacketInKeepAlive(final Object packet) {
        super(packet);
    }

    public static void init() {
        try {
            integerPresent = new FieldResolver(PacketTypeClasses.Client.KEEP_ALIVE)
                    .resolveSilent(int.class, 0)
                    .exists();
        } catch (Exception ignored) {
            // Packet got removed.
        }
    }

    @Override
    protected void setup() {
        if (!integerPresent) {
            this.id = readLong(0);
        } else {
            this.id = readInt(0);
        }
    }
}
