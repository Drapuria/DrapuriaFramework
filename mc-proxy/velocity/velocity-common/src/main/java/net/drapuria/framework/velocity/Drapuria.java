/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.velocity.impl.ComponentHolderDrapuriaVelocityListener;
import net.drapuria.framework.velocity.impl.VelocityDrapuriaPlatform;
import net.drapuria.framework.velocity.impl.VelocityEventHandler;
import net.drapuria.framework.velocity.impl.VelocityTaskScheduler;
import net.drapuria.framework.velocity.plugin.VelocityClassPathAppender;

import java.nio.file.Path;

public class Drapuria {


    public static Object PLUGIN;
    public static ProxyServer SERVER;
    public static boolean SHUTTING_DOWN;
    public static PluginClassLoader CLASS_LOADER;
    public static Path PLUGIN_DIRECTORY;

    public static boolean hasBooted = false;

    public static void init(Object plugin, ProxyServer server, Path path) {
        PLUGIN = plugin;
        PLUGIN_DIRECTORY = path;
        SERVER = server;
        Drapuria.CLASS_LOADER = new PluginClassLoader(plugin.getClass().getClassLoader(), new VelocityClassPathAppender(server, plugin));
        Drapuria.initCommon();
        hasBooted = true;
    }

    private static void initCommon() {

        ComponentRegistry.registerComponentHolder(new ComponentHolderDrapuriaVelocityListener(SERVER));
        DrapuriaCommon.builder()
                .platform(new VelocityDrapuriaPlatform())
                .eventHandler(new VelocityEventHandler())
                .taskScheduler(new VelocityTaskScheduler(PLUGIN, SERVER))
                .init();
    }

    @SneakyThrows
    public static void shutdown() {
        SHUTTING_DOWN = true;
        DrapuriaCommon.shutdown();
        PluginManager.INSTANCE.callFrameworkFullyDisable();
    }
}
