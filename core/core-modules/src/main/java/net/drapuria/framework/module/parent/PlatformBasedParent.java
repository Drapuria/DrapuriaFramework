package net.drapuria.framework.module.parent;


import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class PlatformBasedParent implements ModuleParent<DrapuriaPlatform> {
    @Override
    public File getDataFolder() {
        return DrapuriaCommon.PLATFORM.getDataFolder();
    }

    public Logger getLogger() {
        return DrapuriaCommon.PLATFORM.getLogger();
    }

    @Override
    public DrapuriaPlatform getParent() {
        return DrapuriaCommon.PLATFORM;
    }

    @Override
    public String getParentName() {
        return "DrapuriaFramework";
    }

    @Override
    public ModuleClassLoader createModuleClassLoader() {
        return null;
    }
}
