package net.drapuria.framework.module.scanner.data;

import lombok.Data;
import net.drapuria.framework.module.ModuleAdapter;

@Data
public class EnvironmentScanMissingDependenciesData {

    private final ModuleAdapter adapter;
    private final MissingDependencyData[] missingDependencies;

}
