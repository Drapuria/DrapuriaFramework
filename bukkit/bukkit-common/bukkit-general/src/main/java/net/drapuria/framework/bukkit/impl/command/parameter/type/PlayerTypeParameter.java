/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.command.parameter.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlayerTypeParameter extends CommandTypeParameter<Player>{
    @Override
    public Player parseNonPlayer(CommandSender sender, String source) {
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Bukkit.getOnlinePlayers().stream().filter(player::canSee).map(HumanEntity::getName)
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
    }

    @Override
    public List<String> tabCompleteNonPlayer(CommandSender sender, Set<String> flags, String source) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
    }

    @Override
    public Player parse(Player player, String source) {
        if (source.isEmpty())
            return null;
        return (source.equalsIgnoreCase(Parameter.CURRENT_SELF) ? player : Bukkit.getPlayer(source));
    }

    @Override
    public Class<Player> getType() {
        return Player.class;
    }
}
