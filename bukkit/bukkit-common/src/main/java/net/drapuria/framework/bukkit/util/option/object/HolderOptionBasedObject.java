package net.drapuria.framework.bukkit.util.option.object;

import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;

import java.util.HashMap;
import java.util.Map;

public abstract class HolderOptionBasedObject {

    private final Map<String, AbstractHolderOption<?, ?>> options = new HashMap<>();


    public abstract void registerOptions();

}
