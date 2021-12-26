package net.drapuria.framework.bukkit.impl.module.scanners;

import net.drapuria.framework.bukkit.impl.module.annotation.PluginDependencies;
import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.scanner.EnvironmentScanner;
import net.drapuria.framework.module.scanner.data.EnvironmentScanMissingDependenciesData;
import net.drapuria.framework.module.scanner.data.MissingDependencyData;
import net.drapuria.framework.module.service.ModuleService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class PluginDependenciesScanner extends EnvironmentScanner {
    public PluginDependenciesScanner(ModuleService moduleService, ModuleAdapter moduleAdapter) {
        super(moduleService, moduleAdapter);
    }

    @Override
    public void scan() {
        Class<? extends Module> moduleClass = moduleAdapter.getModule().getClass();
        if (moduleClass.isAnnotationPresent(PluginDependencies.class)) {
            PluginDependencies dependencies = moduleClass.getAnnotation(PluginDependencies.class);
            for (String dependency : dependencies.pluginDependencies()) {
                final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(dependency);
                if (plugin == null || !plugin.isEnabled()) {
                    moduleAdapter.getClassLoader().getModuleParent().getLogger()
                            .error("[Drapuria-Module] Bukkit Plugin " + dependency
                                    + " is not loaded. Not enabling " + this.moduleAdapter.getModuleData().name());
                    setCanEnable(false);
                    return;
                }
            }
        }
        setCanEnable(true);
    }

    @Override
    public void validateDependencies() {
        Class<? extends Module> moduleClass = moduleAdapter.getModule().getClass();
        final Set<MissingDependencyData> missingDependencies = new HashSet<>();
        if (moduleClass.isAnnotationPresent(PluginDependencies.class)) {
            PluginDependencies dependencies = moduleClass.getAnnotation(PluginDependencies.class);
            for (String dependency : dependencies.pluginDependencies()) {
                final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(dependency);
                if (plugin == null || !plugin.isEnabled()) {
                    missingDependencies.add(new MissingDependencyData("BukkitPlugin", dependency));
                }
            }
        }
        if (!missingDependencies.isEmpty()) {
            super.missingDependenciesData = new EnvironmentScanMissingDependenciesData(moduleAdapter,
                    missingDependencies.toArray(new MissingDependencyData[]{}));
        }
    }
}
