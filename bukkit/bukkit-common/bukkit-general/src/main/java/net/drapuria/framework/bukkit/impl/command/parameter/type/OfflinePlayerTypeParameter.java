/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OfflinePlayerTypeParameter extends CommandTypeParameter<OfflinePlayer> {
    @Override
    public OfflinePlayer parseNonPlayer(CommandSender sender, String source) {
        return Bukkit.getOfflinePlayer(source);
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return DrapuriaOfflinePlayerParameter.getOfflinePlayerTabComplete(player, flags, source);
    }

    @Override
    public List<String> tabCompleteNonPlayer(CommandSender sender, Set<String> flags, String source) {
        if (flags.contains("online")) {
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
        }
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
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
