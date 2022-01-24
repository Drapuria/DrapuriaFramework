/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import com.comphenix.protocol.events.PacketContainer;

public interface SendableWrapper {

    default PacketContainer asProtocolLibPacketContainer() {
        return null;
    }

    default Object asNMSPacket() {
        return null;
    }

}
