package net.drapuria.framework.bukkit.protocol.packet;

import org.bukkit.entity.Player;

public interface PacketListener {

    Class<?>[] type();

    default boolean read(Player player, PacketDto packetDto) {
        return true;
    }

    default boolean write(Player player, PacketDto packetDto) {
        return true;
    }

}
