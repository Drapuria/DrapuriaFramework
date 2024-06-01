/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.helper;

import com.comphenix.protocol.events.PacketContainer;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.util.Skin;
import org.bukkit.entity.Player;

public interface VersionHelper<T> {


    Object getScoreboardTeamOptional(String name,
                                     String displayName,
                                     String prefix,
                                     String suffix,
                                     int chatFomat,
                                     int options,
                                     boolean friendlyFire,
                                     WrappedPacketOutScoreboardTeam.NameTagVisibility visibility);

    Object getChatFormat(String str);

    Skin getSkinFromPlayer(final Player player);

    // SCOREBOARD_DISPLAY_OBJECTIVE
    Class<T> getDisplaySlotEnum();

    T translateDisplaySlot(int slot);

}
