/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.ImmutableBiMap;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import org.bukkit.scoreboard.DisplaySlot;

@Getter
@Setter
public class WrappedPacketOutScoreboardDisplayObjective implements SendableWrapper {

    private static ImmutableBiMap<DisplaySlot, Integer> DISPLAY_SLOT_TO_ID;

    static {
        DISPLAY_SLOT_TO_ID = ImmutableBiMap.<DisplaySlot, Integer>builder()
                .put(DisplaySlot.PLAYER_LIST, 0)
                .put(DisplaySlot.SIDEBAR, 1)
                .put(DisplaySlot.BELOW_NAME, 2)
                .build();

    }

    private DisplaySlot displaySlot;
    private String objective;
    @Override
    public PacketContainer asProtocolLibPacketContainer() {
        final PacketContainer packetContainer = ProtocolLibService.getService.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        packetContainer.getIntegers().write(0, DISPLAY_SLOT_TO_ID.get(this.displaySlot));
        packetContainer.getStrings().write(0, this.objective);
        return packetContainer;
    }
}