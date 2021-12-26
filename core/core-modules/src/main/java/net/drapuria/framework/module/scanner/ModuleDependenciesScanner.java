package net.drapuria.framework.module.scanner;

import net.drapuria.framework.module.JavaModule;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.scanner.data.EnvironmentScanMissingDependenciesData;
import net.drapuria.framework.module.scanner.data.MissingDependencyData;
import net.drapuria.framework.module.service.ModuleService;

import java.util.HashSet;
import java.util.Set;

public class ModuleDependenciesScanner extends EnvironmentScanner{
    public ModuleDependenciesScanner(ModuleService moduleService, ModuleAdapter moduleAdapter) {
        super(moduleService, moduleAdapter);
    }

    @Override
    public void scan() {
        for (String dependency : moduleAdapter.getModuleData().moduleDependencies()) {
            final JavaModule depend = (JavaModule) moduleService.getGlobalModuleRepository().findByName(dependency);
            if (depend == null || !depend.isEnabled()) {
                setCanEnable(false);
                return;
            }
        }
        setCanEnable(true);
    }

    @Override
    public void validateDependencies() {
        final Set<MissingDependencyData> missingDependencies = new HashSet<>();
        for (String dependency : moduleAdapter.getModuleData().moduleDependencies()) {
            final ModuleAdapter depend =  moduleService.getGlobalModuleRepository().findAdapterByName(dependency);
            if (depend == null) {
               missingDependencies.add(new MissingDependencyData("Module", dependency));
            }
        }
        if (!missingDependencies.isEmpty()) {
           super.missingDependenciesData = new EnvironmentScanMissingDependenciesData(moduleAdapter,
                   missingDependencies.toArray(new MissingDependencyData[]{}));
        }
    }
}
