/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.input;

import net.drapuria.framework.bukkit.util.option.OptionContext;
import org.bukkit.entity.Player;

public abstract class AbstractOptionInput<C, T, O extends OptionContext<?, C, T>> {

    public abstract <O extends OptionContext<?, C, T>> void startInput(Player player, O optionContext);

    public abstract <O extends OptionContext<?, C, T>> void endInput(Player player, boolean success, O optionContext);

}
