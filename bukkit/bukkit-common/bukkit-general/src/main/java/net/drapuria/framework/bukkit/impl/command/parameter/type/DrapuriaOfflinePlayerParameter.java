/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.bukkit.player.DrapuriaOfflinePlayer;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DrapuriaOfflinePlayerParameter extends CommandTypeParameter<DrapuriaOfflinePlayer> {

    public static final String SELF_PLAYER = "§SELF";


    private final Map<String, DrapuriaOfflinePlayer> playerCache = new HashMap<>();

    @Override
    public DrapuriaOfflinePlayer parseNonPlayer(CommandSender sender, String source) {
        if (playerCache.containsKey(source)) {
            return playerCache.get(source);
        }
        DrapuriaOfflinePlayer player;
        playerCache.put(source, player = new DrapuriaOfflinePlayer(source));
        return player;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return getOfflinePlayerTabComplete(player, flags, source);
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

    @NotNull
    static List<String> getOfflinePlayerTabComplete(Player player, Set<String> flags, String source) {
        if (flags.contains("online")) {
            return Bukkit.getOnlinePlayers().stream().filter(player::canSee).map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
        }
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(offlinePlayer -> !offlinePlayer.isOnline() || player.canSee(offlinePlayer.getPlayer()))
                .map(OfflinePlayer::getName)
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
    }

    @Override
    public DrapuriaOfflinePlayer parse(Player sender, String source) {
        if (source.equals(SELF_PLAYER)) source = sender.getName();
        if (playerCache.containsKey(source)) {
            return playerCache.get(source);
        }
        DrapuriaOfflinePlayer player;
        playerCache.put(source, player = new DrapuriaOfflinePlayer(source));
        return player;
    }

    @Override
    public Class<DrapuriaOfflinePlayer> getType() {
        return DrapuriaOfflinePlayer.class;
    }
}
