package net.drapuria.framework.bungee;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bungee.impl.BungeeDrapuriaPlatform;
import net.drapuria.framework.bungee.impl.BungeeEventHandler;
import net.drapuria.framework.bungee.impl.BungeeTaskScheduler;
import net.drapuria.framework.bungee.impl.ComponentHolderBungeeListener;
import net.drapuria.framework.jackson.libraries.annotation.MavenDependency;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@MavenDependency(groupId = "org.json", artifactId = "json", version = "20200518")
public class Drapuria {

    public static final Logger LOGGER = LogManager.getLogger("Drapuria");

    public static Plugin PLUGIN;

    public static boolean SHUTTING_DOWN;
    public static PluginClassLoader CLASS_LOADER;

    public static void init(Plugin plugin) {
        Drapuria.PLUGIN = plugin;
        Drapuria.CLASS_LOADER = new PluginClassLoader(plugin.getClass().getClassLoader());
        Drapuria.initCommon();
    }

    private static void initCommon() {
        ComponentRegistry.registerComponentHolder(new ComponentHolderBungeeListener());
        DrapuriaCommon.builder()
                .platform(new BungeeDrapuriaPlatform())
                .eventHandler(new BungeeEventHandler())
                .taskScheduler(new BungeeTaskScheduler(PLUGIN))
                .init();
    }

    public static ProxyServer getProxy() {
        return ProxyServer.getInstance();
    }

    @SneakyThrows
    public static void shutdown() {
        SHUTTING_DOWN = true;
        DrapuriaCommon.shutdown();
        PluginManager.INSTANCE.callFrameworkFullyDisable();
    }

}
