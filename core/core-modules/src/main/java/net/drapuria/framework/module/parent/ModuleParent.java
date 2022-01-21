/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module.parent;

import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.service.ModuleService;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;

public interface ModuleParent<P> {

    /**
     * @return Datafolder of the parent
     */
    File getDataFolder();

    /**
     * @return Logger of the parent
     */
    Logger getLogger();

    /**
     * @return Parent
     */
    P getParent();

    /**
     * @return name of the parent
     */
    String getParentName();

    /**
     * @param file The Module FIle
     * @param moduleService Service class
     * @return The ClassLoader of the module
     * @throws MalformedURLException throws exception if class loader could not load
     */
    ModuleClassLoader createModuleClassLoader(File file, ModuleService moduleService) throws MalformedURLException;

    /**
     * @param module The Module
     * @param moduleData The Module Data
     * @return The Module
     */
    Module createModule(Module module, ModuleData moduleData);

}
