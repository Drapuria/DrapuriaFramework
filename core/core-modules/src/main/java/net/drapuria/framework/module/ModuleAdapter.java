package net.drapuria.framework.module;

import lombok.Data;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.classloader.ModuleClassLoader;

@Data
public class ModuleAdapter {

    private final Module module;
    private final ModuleData moduleData;
    private final ModuleClassLoader classLoader;

}
