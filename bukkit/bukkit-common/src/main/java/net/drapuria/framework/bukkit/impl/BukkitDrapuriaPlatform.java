package net.drapuria.framework.bukkit.impl;

import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.beans.BeanContext;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.spigotmc.RestartCommand;

import java.io.File;

public class BukkitDrapuriaPlatform implements DrapuriaPlatform {
    @Override
    public void saveResources(String name, boolean replace) {
        Drapuria.PLUGIN.saveResource(name, replace);
    }

    @Override
    public PluginClassLoader getClassLoader() {
        return Drapuria.CLASS_LOADER;
    }

    @Override
    public File getDataFolder() {
        return Drapuria.PLUGIN.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return Drapuria.LOGGER;
    }

    @Override
    public void shutdown() {
        RestartCommand.restart();
    }

    @Override
    public boolean isShuttingDown() {
        return Drapuria.SHUTTING_DOWN;
    }

    @Override
    public boolean isServerThread() {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public void registerCommandProvider() {
        CommandService commandService = (CommandService) BeanContext.INSTANCE.getBean(CommandService.class);
        commandService.registerCommandProvider(new BukkitCommandProvider(commandService));
    }
}
