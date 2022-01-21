/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module.scanner;

import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.scanner.data.EnvironmentScanMissingDependenciesData;
import net.drapuria.framework.module.service.ModuleService;

public abstract class EnvironmentScanner {

    protected final ModuleAdapter moduleAdapter;
    protected final ModuleService moduleService;

    private boolean canEnable = false;
    protected EnvironmentScanMissingDependenciesData missingDependenciesData = null;

    public EnvironmentScanner(final ModuleService moduleService, final ModuleAdapter moduleAdapter) {
        this.moduleService = moduleService;
        this.moduleAdapter = moduleAdapter;
    }


    public abstract void scan();

    public abstract void validateDependencies();


    public boolean canEnable() {
        return this.canEnable;
    }
    public void setCanEnable(boolean canEnable) {
        this.canEnable = canEnable;
    }

    public EnvironmentScanMissingDependenciesData getMissingDependenciesData() {
        return missingDependenciesData;
    }

    public ModuleAdapter getModuleAdapter() {
        return moduleAdapter;
    }
}
