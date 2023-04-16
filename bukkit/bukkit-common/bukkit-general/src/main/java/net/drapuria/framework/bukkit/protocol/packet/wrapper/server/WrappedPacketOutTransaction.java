package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@AutowiredWrappedPacket(value = PacketType.Server.TRANSACTION, direction = PacketDirection.WRITE)
@Setter
@Getter
public class WrappedPacketOutTransaction extends WrappedPacket implements SendableWrapper {

    private static Class<?> packetClass;
    private static Constructor<?> packetConstructor;

    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public WrappedPacketOutTransaction(final Object packet) {
        super(packet);
    }

    public WrappedPacketOutTransaction(final int windowId, final short actionNumber, final boolean accepted) {
        super();
        this.windowId = windowId;
        this.actionNumber = actionNumber;
        this.accepted = accepted;
    }

    public static void init() {
        packetClass = PacketTypeClasses.Server.TRANSACTION;

        try {
            packetConstructor = packetClass.getConstructor(int.class, short.class, boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setup() {
        this.windowId = readInt(0);
        this.actionNumber = readShort(0);
        this.accepted = readBoolean(0);

    }

    @Override
    public Object asNMSPacket() {
        try {
            return packetConstructor.newInstance(windowId, actionNumber, accepted);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
