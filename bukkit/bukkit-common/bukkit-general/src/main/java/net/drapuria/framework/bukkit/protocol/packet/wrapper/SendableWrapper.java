/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.wrapper;


public interface SendableWrapper {

    default com.comphenix.protocol.events.PacketContainer asProtocolLibPacketContainer() {
        return null;
    }

    default PacketContainer asPacketContainer() {
        return PacketContainer.of(asNMSPacket());
    }

    default Object asNMSPacket() {
        return null;
    }

}
