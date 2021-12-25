package net.drapuria.framework.bukkit.impl.module.parent;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.module.parent.ModuleParent;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BukkitPluginBasedParent implements ModuleParent<Plugin> {

    private final Plugin plugin;

    public BukkitPluginBasedParent(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return DrapuriaCommon.getLogger();
    }

    @Override
    public Plugin getParent() {
        return plugin;
    }

    @Override
    public String getParentName() {
        return plugin.getName();
    }
}
