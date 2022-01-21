/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.module;

import lombok.Getter;
import net.drapuria.framework.module.JavaModule;
import org.bukkit.plugin.Plugin;

@Getter
public abstract class BukkitModule extends JavaModule {

    private Plugin plugin = null;

}
