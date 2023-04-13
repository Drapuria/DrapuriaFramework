/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.plugin;

import java.io.File;

public interface AbstractPlugin {

    default void onInitial() {

    }

    default void onPreEnable() {

    }

    default void onPluginEnable() {

    }

    default void onPluginDisable() {

    }

    default void onFrameworkFullyDisable() {

    }

    void close();

    ClassLoader getPluginClassLoader();

    String getName();

    File getDataFolder();

}
