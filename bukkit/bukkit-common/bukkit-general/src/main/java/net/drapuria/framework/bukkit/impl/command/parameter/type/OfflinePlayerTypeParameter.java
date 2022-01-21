/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

@Component
public class OfflinePlayerTypeParameter extends CommandTypeParameter<OfflinePlayer> {
    @Override
    public OfflinePlayer parseNonPlayer(CommandSender sender, String value) {
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return DrapuriaOfflinePlayerParameter.getOfflinePlayerTabComplete(player, flags, source);
    }

    @Override
    public OfflinePlayer parse(Player sender, String source) {
        return Bukkit.getOfflinePlayer(source);
    }

    @Override
    public Class<OfflinePlayer> getType() {
        return OfflinePlayer.class;
    }
}
