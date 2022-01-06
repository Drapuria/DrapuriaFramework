package net.drapuria.framework.bungee.plugin;

import net.drapuria.framework.bungee.Drapuria;
import net.drapuria.framework.bungee.impl.BungeePluginHandler;
import net.drapuria.framework.plugin.PluginManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeStandalonePlugin extends Plugin {

    @Override
    public void onLoad() {
        PluginManager.initialize(new BungeePluginHandler());
    }

    @Override
    public void onEnable() {
        final ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Drapuria.init(this);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }
}
