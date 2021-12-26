package net.drapuria.framework.bukkit.plugin.example;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.impl.module.parent.BukkitPluginBasedParent;
import net.drapuria.framework.bukkit.impl.module.repository.BukkitPluginModuleRepository;
import net.drapuria.framework.bukkit.plugin.BukkitPlugin;
import net.drapuria.framework.module.service.ModuleService;

import java.io.File;

public class ExamplePlugin extends BukkitPlugin {

    @Override
    public void onPluginEnable() {
        DrapuriaCommon.TASK_SCHEDULER.runSync(this::loadModules);
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
