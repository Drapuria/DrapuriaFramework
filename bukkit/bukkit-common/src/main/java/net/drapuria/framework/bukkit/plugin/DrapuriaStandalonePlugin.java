package net.drapuria.framework.bukkit.plugin;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.libraries.annotation.MavenDependency;
import net.drapuria.framework.libraries.annotation.MavenRepository;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public final class DrapuriaStandalonePlugin extends JavaPlugin {

    public DrapuriaStandalonePlugin() {
        super();
    }

    public DrapuriaStandalonePlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onLoad() {
        Drapuria.preInit();
    }

    @Override
    public void onEnable() {
        final ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        Drapuria.init(this);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void onDisable() {
        Drapuria.shutdown();
    }
}
