package net.drapuria.framework.bukkit.plugin;

import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin implements AbstractPlugin {

    @Override
    public final void onLoad() {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        PluginManager.INSTANCE.addPlugin(this);
        PluginManager.INSTANCE.onPluginInitial(this);

        this.onInitial();
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void onEnable() {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());

        Utility.resolveLinkageError();
        this.onPreEnable();
        PluginManager.INSTANCE.onPluginEnable(this);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        this.onPluginEnable();
    }

    @Override
    public void onDisable() {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        this.onPluginDisable();
        PluginManager.INSTANCE.onPluginDisable(this);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void close() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return this.getClassLoader();
    }
}
