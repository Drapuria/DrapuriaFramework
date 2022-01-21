/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module.parent;


import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.module.JavaModule;
import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.classloader.PlatformBasedModuleClassLoader;
import net.drapuria.framework.module.service.ModuleService;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;

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
    public ModuleClassLoader createModuleClassLoader(File file, ModuleService moduleService) throws MalformedURLException {
        return new PlatformBasedModuleClassLoader(this, file, moduleService, getParent().getClass().getClassLoader());
    }

    @Override
    public Module createModule(Module module, ModuleData moduleData) {
        try {
            Field field = JavaModule.class.getDeclaredField("moduleParent");
            field.setAccessible(true);
            field.set(module, this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return module;
    }
}
