/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.helper;

import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacketOutScoreboardTeam;

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


}
