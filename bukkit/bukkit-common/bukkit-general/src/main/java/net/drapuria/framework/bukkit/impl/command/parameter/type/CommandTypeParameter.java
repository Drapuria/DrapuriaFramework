/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.command.parser.CommandTypeParameterParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * @param <T> Type to transform
 */
public abstract class CommandTypeParameter<T> implements CommandTypeParameterParser<T, Player> {

    /**
     * @param sender The Command sender (
     * @param value  The value as a string
     * @return T as  the parsed value
     */
    public abstract T parseNonPlayer(CommandSender sender, String value);

    /**
     * @param player The player who is performing the tab complete
     * @param flags given tab complete flags by the parameter data
     * @param source the current string
     * @return
     */
    public abstract List<String> tabComplete(Player player, Set<String> flags, String source);

}
