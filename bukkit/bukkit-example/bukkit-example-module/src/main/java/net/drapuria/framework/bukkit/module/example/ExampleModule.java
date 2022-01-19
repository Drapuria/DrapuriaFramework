package net.drapuria.framework.bukkit.module.example;

import net.drapuria.framework.bukkit.impl.module.BukkitModule;
import net.drapuria.framework.bukkit.impl.module.annotation.PluginDependencies;
import net.drapuria.framework.module.annotations.ModuleData;

@ModuleData(
        name = "ExampleModule",
        version = "1.0",
        author = "Drapuria Development Team",
        description = {"Example Module for the", "Drapuria Framework"}
)
@PluginDependencies(pluginDependencies = "Pinger")
public class ExampleModule extends BukkitModule {

    @Override
    public void onLoad() {
        getModuleParent().getLogger().info("Example Module Loaded");
    }

    @Override
    public void onEnable() {
        getModuleParent().getLogger().info("Example Module Enabled");
    }

    @Override
    public void onDisable() {
        getModuleParent().getLogger().info("Example Module Disabled");
    }

}
