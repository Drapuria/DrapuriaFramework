/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module;

import net.drapuria.framework.module.parent.ModuleParent;

import java.io.File;

public interface Module {

    void onLoad();

    void onEnable();

    void onDisable();

    File getDataFolder();

    String getName();

    ModuleParent<?> getModuleParent();

}
