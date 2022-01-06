package net.drapuria.framework.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.velocity.impl.VelocityDrapuriaPlatform;
import net.drapuria.framework.velocity.impl.VelocityEventHandler;
import net.drapuria.framework.velocity.impl.VelocityTaskScheduler;
import net.drapuria.framework.velocity.plugin.VelocityPlugin;

import java.nio.file.Path;

public class Drapuria {


    public static VelocityPlugin PLUGIN;
    public static ProxyServer SERVER;
    public static boolean SHUTTING_DOWN;
    public static PluginClassLoader CLASS_LOADER;
    public static Path PLUGIN_DIRECTORY;

    public static void init(VelocityPlugin plugin, ProxyServer server, Path path) {
        PLUGIN = plugin;
        PLUGIN_DIRECTORY = path;
        SERVER = server;
        Drapuria.CLASS_LOADER = new PluginClassLoader(plugin.getClass().getClassLoader());
        Drapuria.initCommon();
    }

    private static void initCommon() {
        DrapuriaCommon.builder()
                .platform(new VelocityDrapuriaPlatform())
                .eventHandler(new VelocityEventHandler())
                .taskScheduler(new VelocityTaskScheduler(PLUGIN, SERVER))
                .init();
    }

}
