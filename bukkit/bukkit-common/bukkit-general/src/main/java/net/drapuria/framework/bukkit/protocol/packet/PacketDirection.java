package net.drapuria.framework.bukkit.protocol.packet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.ConstructorResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ConstructorWrapper;
import org.bukkit.entity.Player;


import java.util.Map;

public enum PacketDirection {

    READ,
    WRITE;

    private final Multimap<Byte, PacketListener> registeredPacketListeners = HashMultimap.create();
    private Map<Byte, Class<? extends WrappedPacket>> typeToWrappedPacket;

    public void register(Map<Byte, Class<? extends WrappedPacket>> typeToWrappedPacket) {
        if (this.typeToWrappedPacket != null) {
            throw new IllegalStateException("The Wrapped Packet are already registered!");
        }
        this.typeToWrappedPacket = typeToWrappedPacket;
    }

    public byte getPacketType(Object packet) {

        switch (this) {

            case READ:
                return PacketType.Client.getIdByType(packet.getClass());

            case WRITE:
                return PacketType.Server.getIdByType(packet.getClass());

        }

        return -1;

    }

    public boolean isPacketListening(byte id) {
        return this.registeredPacketListeners.containsKey(id);
    }

    public WrappedPacket getWrappedFromNMS(Player player, byte id, Object packet) {

        Class<? extends WrappedPacket> wrappedPacketClass = this.typeToWrappedPacket.getOrDefault(id, null);

        if (wrappedPacketClass == null) {
            return new WrappedPacket(player, packet);
        }


        ConstructorResolver constructorResolver = new ConstructorResolver(wrappedPacketClass);
        ConstructorWrapper<? extends WrappedPacket> constructor = constructorResolver.resolveWrapper(new Class[] { Player.class, Object.class });

        if (constructor.exists()) {
            return constructor.newInstance(player, packet);
        }

        constructor = constructorResolver.resolveWrapper(new Class[] { Object.class });

        if (constructor.exists()) {
            return constructor.newInstance(packet);
        }

        throw new IllegalArgumentException();
    }

    public WrappedPacket getWrappedFromNMS(Player player, byte id) {

        Class<? extends WrappedPacket> wrappedPacketClass = this.typeToWrappedPacket.getOrDefault(id, null);

        if (wrappedPacketClass == null) {
            return new WrappedPacket(player);
        }

        return (WrappedPacket)  new ConstructorResolver(wrappedPacketClass)
                .resolveMatches(
                        new Class[] { Player.class },
                        new Class[0])
                .resolve(
                        new Object[] { player },
                        new Object[0]
                );

    }

}
