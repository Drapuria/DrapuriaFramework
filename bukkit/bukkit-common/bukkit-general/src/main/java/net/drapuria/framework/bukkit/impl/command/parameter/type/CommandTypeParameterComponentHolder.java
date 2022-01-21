/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.beans.component.ComponentHolder;

public class CommandTypeParameterComponentHolder extends ComponentHolder {

    private final BukkitCommandProvider provider;

    public CommandTypeParameterComponentHolder(BukkitCommandProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onEnable(Object instance) {
        if (provider == null)
            return;
        provider.registerTypeParameter((CommandTypeParameter<?>) instance);
    }

    @Override
    public void onDisable(Object instance) {
        if (provider == null)
            return;
        provider.unregisterTypeParameterParser((CommandTypeParameter<?>) instance);
    }

    @Override
    public Class<?>[] type() {
        return new Class[] {CommandTypeParameter.class};
    }
}
