/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.holder.input;

import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;
import org.bukkit.ChatColor;

public class ColoredChatHolderOptionInput<V, H, O extends AbstractHolderOption<V, H>> extends ChatHolderOptionInput<V, H, O> {
    @Override
    public String parseInput(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
