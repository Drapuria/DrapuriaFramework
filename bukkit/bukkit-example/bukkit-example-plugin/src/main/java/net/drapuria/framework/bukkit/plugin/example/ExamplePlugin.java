package net.drapuria.framework.bukkit.plugin.example;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.bukkit.impl.module.parent.BukkitPluginBasedParent;
import net.drapuria.framework.bukkit.impl.module.repository.BukkitPluginModuleRepository;
import net.drapuria.framework.bukkit.plugin.BukkitPlugin;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.module.service.ModuleService;
import net.drapuria.framework.services.ClasspathScan;

import java.io.File;

@ClasspathScan("net.drapuria.framework.bukkit.plugin.example")
public class ExamplePlugin extends BukkitPlugin {

    @Override
    public void onPluginEnable() {
        DrapuriaCommon.TASK_SCHEDULER.runSync(this::loadModules);
        CommandService commandService = (CommandService) DrapuriaCommon.BEAN_CONTEXT.getBean(CommandService.class);
        BukkitCommandProvider bukkitCommandProvider = (BukkitCommandProvider) commandService.getCommandProvider();
        bukkitCommandProvider.loadCommands(this, "net.drapuria.framework.bukkit.plugin.example");
    }

    @Override
    public void onPluginDisable() {
        super.onPluginDisable();
    }

    private void loadModules() {
        final ModuleService moduleService = (ModuleService) DrapuriaCommon.BEAN_CONTEXT.getBean(ModuleService.class);
        final BukkitPluginBasedParent moduleParent = new BukkitPluginBasedParent(this);
        moduleService.loadModules(new File(getDataFolder(), "modules"), moduleParent,
                new BukkitPluginModuleRepository(moduleParent));
    }

}
