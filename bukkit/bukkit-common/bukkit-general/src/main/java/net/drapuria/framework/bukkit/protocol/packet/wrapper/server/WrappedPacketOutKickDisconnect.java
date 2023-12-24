package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@AutowiredWrappedPacket(value = PacketType.Server.KICK_DISCONNECT, direction = PacketDirection.WRITE)
@Getter
public final class WrappedPacketOutKickDisconnect extends WrappedPacket implements SendableWrapper {
    private static Class<?> packetClass, iChatBaseComponentClass;
    private static Constructor<?> kickDisconnectConstructor;
    private String kickMessage;

    public WrappedPacketOutKickDisconnect(final Object packet) {
        super(packet);
    }

    public WrappedPacketOutKickDisconnect(final String kickMessage) {
        super();
        this.kickMessage = kickMessage;
    }

    public static void init() {
        packetClass = PacketTypeClasses.Server.KICK_DISCONNECT;
        try {
            iChatBaseComponentClass = NMS_CLASS_RESOLVER.resolve("IChatBaseComponent", "network.chat.IChatBaseComponent");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            kickDisconnectConstructor = packetClass.getConstructor(iChatBaseComponentClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setup() {
        Object ichatbaseComponentoObject = readObject(0, iChatBaseComponentClass);
        this.kickMessage = WrappedPacketOutChat.toStringFromIChatBaseComponent(ichatbaseComponentoObject);
    }

    @Override
    public Object asNMSPacket() {
        try {
            return kickDisconnectConstructor.newInstance(WrappedPacketOutChat.toIChatBaseComponent(WrappedPacketOutChat.fromStringToJSON(kickMessage)));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
