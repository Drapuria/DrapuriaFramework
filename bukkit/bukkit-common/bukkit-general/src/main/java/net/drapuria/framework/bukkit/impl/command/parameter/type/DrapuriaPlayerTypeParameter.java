/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import net.drapuria.framework.command.parameter.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DrapuriaPlayerTypeParameter extends CommandTypeParameter<DrapuriaPlayer> {

    private static final PlayerRepository playerRepository = PlayerRepository.getRepository;

    @Override
    public DrapuriaPlayer parseNonPlayer(CommandSender sender, String value) {
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Bukkit.getOnlinePlayers().stream().filter(player::canSee).map(HumanEntity::getName)
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
    }

    @Override
    public DrapuriaPlayer parse(Player player, String source) {
        if (source.equalsIgnoreCase(Parameter.CURRENT_SELF)) {
            return playerRepository.findById(player.getUniqueId()).get();
        }
        final Player target = Bukkit.getPlayer(source);
        final Optional<DrapuriaPlayer> optional = source.equalsIgnoreCase(Parameter.CURRENT_SELF) ? playerRepository.findById(player.getUniqueId()) : target == null ? Optional.empty() : playerRepository.findById(target.getUniqueId());
        return optional.orElse(null);
    }

    @Override
    public Class<DrapuriaPlayer> getType() {
        return DrapuriaPlayer.class;
    }
}
