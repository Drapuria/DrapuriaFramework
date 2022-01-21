/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bungee.plugin;

import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.util.Utility;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class BungeePlugin extends Plugin implements AbstractPlugin {

    @Override
    public void onLoad() {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getPluginClassLoader());
        PluginManager.INSTANCE.addPlugin(this);
        PluginManager.INSTANCE.onPluginInitial(this);
        this.onInitial();
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void onEnable() {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getPluginClassLoader());

        Utility.resolveLinkageError();

        this.onPreEnable();

        PluginManager.INSTANCE.onPluginEnable(this);

        this.onPluginEnable();
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void onDisable() {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getPluginClassLoader());
        this.onPluginDisable();

        PluginManager.INSTANCE.onPluginDisable(this);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void close() {
        //
    }

    @Override
    public final ClassLoader getPluginClassLoader() {
        return this.getClass().getClassLoader();
    }

}
