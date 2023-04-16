package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ObjectWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;

import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;


import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@AutowiredWrappedPacket(value = PacketType.Server.POSITION, direction = PacketDirection.WRITE)
@Getter
@Setter
public final class WrappedPacketOutPosition extends WrappedPacket implements SendableWrapper {

    private static Class<?> packetClass;
    private static Class<? extends Enum> enumPlayerTeleportFlagClass;

    private static boolean hasFlags;
    private static boolean hasTeleportId;

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<EnumPlayerTeleportFlags> flags;

    // 1.7
    private boolean onGround;

    // around 1.12
    private int teleportId;

    public WrappedPacketOutPosition(Object packet) {
        super(packet);
    }

    public WrappedPacketOutPosition(Player player, Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();

        if (hasFlags) {
            this.flags = Collections.emptySet();
        } else {
            this.onGround = player.isOnGround();
        }

        if (hasTeleportId) {
            this.teleportId = 0;
        }
    }

    public static void init() {
        packetClass = PacketTypeClasses.Server.POSITION;

        hasFlags = true;

        try {
            enumPlayerTeleportFlagClass = NMS_CLASS_RESOLVER.resolveSubClass(packetClass, "EnumPlayerTeleportFlags");
        } catch (ClassNotFoundException throwable) {

            try {
                enumPlayerTeleportFlagClass = NMS_CLASS_RESOLVER.resolve("EnumPlayerTeleportFlags");
            } catch (ClassNotFoundException throwable2) {
                hasFlags = false;
            }
        }

        hasTeleportId = new FieldResolver(packetClass)
                .resolveSilent(int.class, 0)
                .exists();
    }

    @Override
    protected void setup() {
        this.x = readDouble(0);
        this.y = readDouble(1);
        this.z = readDouble(2);

        this.yaw = readFloat(0);
        this.pitch = readFloat(1);

        if (hasFlags) {
            Set<? extends Enum> flagSet = readObject(0, Set.class);
            this.flags = flagSet
                    .stream()
                    .map(Enum::name)
                    .map(EnumPlayerTeleportFlags::valueOf)
                    .collect(Collectors.toSet());
        } else {
            this.onGround = readBoolean(0);
        }

        if (hasTeleportId) {
            this.teleportId = readInt(0);
        }
    }

    @Override
    public Object asNMSPacket() {
        try {
            Object object = packetClass.newInstance();
            ObjectWrapper packet = new ObjectWrapper(object);

            packet.getFieldWrapperByIndex(double.class, 0).set(object, this.x);
            packet.getFieldWrapperByIndex(double.class, 1).set(object, this.y);
            packet.getFieldWrapperByIndex(double.class, 2).set(object, this.z);

            packet.getFieldWrapperByIndex(float.class, 0).set(object, this.yaw);
            packet.getFieldWrapperByIndex(float.class, 1).set(object, this.pitch);

            if (hasFlags) {
                Set flagSet = packet.getFieldByFirstType(Set.class);

                flagSet.addAll(EnumPlayerTeleportFlags.toNMS(this.flags));
            }

            if (hasTeleportId) {
                packet.getFieldWrapperByIndex(int.class, 0).set(object, this.teleportId);
            }

            return object;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static enum EnumPlayerTeleportFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int bit;

        private EnumPlayerTeleportFlags(int bit) {
            this.bit = bit;
        }

        private int getBit() {
            return 1 << this.bit;
        }

        private boolean isSet(int flags) {
            return (flags & this.getBit()) == this.getBit();
        }

        public static Set<?> toNMS(Set<EnumPlayerTeleportFlags> flags) {
            EnumSet nmsFlags = EnumSet.noneOf(enumPlayerTeleportFlagClass);

            for (EnumPlayerTeleportFlags teleportFlags : flags) {
                nmsFlags.add(Enum.valueOf(enumPlayerTeleportFlagClass, teleportFlags.name()));
            }

            return nmsFlags;
        }

        public static Set<EnumPlayerTeleportFlags> unpack(int flags) {
            Set<EnumPlayerTeleportFlags> set = EnumSet.noneOf(EnumPlayerTeleportFlags.class);

            for (EnumPlayerTeleportFlags playerTeleportFlags : values())
            {
                if (playerTeleportFlags.isSet(flags))
                {
                    set.add(playerTeleportFlags);
                }
            }

            return set;
        }

        public static int pack(Set<EnumPlayerTeleportFlags> flags) {
            int i = 0;

            for (EnumPlayerTeleportFlags playerTeleportFlags : flags)
            {
                i |= playerTeleportFlags.getBit();
            }

            return i;
        }
    }
}
