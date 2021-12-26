package net.drapuria.framework.bukkit.impl.module.parent;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.impl.module.BukkitModule;
import net.drapuria.framework.bukkit.impl.module.classloader.BukkitModuleClassLoader;
import net.drapuria.framework.bukkit.util.ReflectionUtils;
import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.service.ModuleService;
import net.drapuria.framework.util.Utility;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.MalformedURLException;

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

    @Override
    public ModuleClassLoader createModuleClassLoader(File file, ModuleService moduleService) throws MalformedURLException {
        return new BukkitModuleClassLoader(this, file, moduleService, getParent().getClass().getClassLoader());
    }

    @Override
    public Module createModule(Module module, ModuleData moduleData) {

        if (Utility.getSuperAndInterfaces(module.getClass()).contains(BukkitModule.class)) {
            BukkitModule bukkitModule = (BukkitModule) module;
            try {
                ReflectionUtils.setValue(bukkitModule, true, "plugin", this.plugin);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            ReflectionUtils.setValue(module, true, "moduleParent", this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return module;
    }

}
