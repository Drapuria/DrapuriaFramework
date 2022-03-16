/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.helper;

import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.util.Skin;
import org.bukkit.entity.Player;

public interface VersionHelper {


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

}
