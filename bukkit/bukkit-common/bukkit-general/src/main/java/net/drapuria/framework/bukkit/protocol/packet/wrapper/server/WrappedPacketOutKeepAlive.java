package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@AutowiredWrappedPacket(value = PacketType.Server.KEEP_ALIVE, direction = PacketDirection.WRITE)
@Getter
public class WrappedPacketOutKeepAlive extends WrappedPacket implements SendableWrapper {
    private static Class<?> packetClass;
    private static Constructor<?> keepAliveConstructor;
    private static boolean integerMode;

    private long id;

    public WrappedPacketOutKeepAlive(Object packet) {
        super(packet);
    }

    public WrappedPacketOutKeepAlive(long id) {
        super();
        this.id = id;
    }

    public static void init() {
        packetClass = PacketTypeClasses.Server.KEEP_ALIVE;
        if (packetClass == null) {
            return;
        }
        integerMode = new FieldResolver(packetClass).resolveSilent(int.class, 0).exists();

        if (integerMode) {
            try {
                keepAliveConstructor = packetClass.getConstructor(int.class);
            } catch (NoSuchMethodException e) {
                if (Minecraft.VERSION != null && Minecraft.VERSION.olderThan(Minecraft.Version.v1_19_R1)) {
                   e.printStackTrace();
                }
             // PACKET GOT REMOVED, WE DO NOT HAVE TO HANDLE THE EXCEPTION
            }
        } else {
            try {
                keepAliveConstructor = packetClass.getConstructor(long.class);
            } catch (NoSuchMethodException e) {
                if (Minecraft.VERSION != null && Minecraft.VERSION.olderThan(Minecraft.Version.v1_19_R1)) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void setup() {
        if(integerMode) {
            this.id = readInt(0);
        }
        else {
            this.id = readLong(0);
        }
    }

    @Override
    public Object asNMSPacket() {
        if (integerMode) {
            try {
                return keepAliveConstructor.newInstance((int) id);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return keepAliveConstructor.newInstance(id);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
