package net.drapuria.framework.module.parent;



import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import org.apache.logging.log4j.Logger;

import java.io.File;

public interface ModuleParent<P> {

    File getDataFolder();

    Logger getLogger();

    P getParent();

    String getParentName();

    ModuleClassLoader createModuleClassLoader();

    Module createModule();

}
