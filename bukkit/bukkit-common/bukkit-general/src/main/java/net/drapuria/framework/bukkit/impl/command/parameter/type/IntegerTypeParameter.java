/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class IntegerTypeParameter extends CommandTypeParameter<Integer> {
    @Override
    public Integer parseNonPlayer(CommandSender sender, String value) {
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Collections.emptyList();
    }

    @Override
    public Integer parse(Player sender, String source) {
        try {
            return Integer.parseInt(source);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
