/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.velocity.Drapuria;
import net.drapuria.framework.velocity.impl.VelocityPluginHandler;

import java.nio.file.Path;

@Plugin(id = "drapuriaframework", name = "Drapuria Framework", version = "${project.version}",
        url = "http://drapuria.net", description = "Drapuria Framework", authors = "Drapuria Development Team")
public class VelocityStandalonePlugin {

    private final ProxyServer server;
    private final Path pluginDirectory;

    @Inject
    public VelocityStandalonePlugin(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.pluginDirectory = dataDirectory;
        this.server = server;
    }

    @Subscribe(async = false, order = PostOrder.FIRST)
    public void onInit(ProxyInitializeEvent event) {
        PluginManager.initialize(new VelocityPluginHandler());

        final ClassLoader pluginClassLoader = this.getClass().getClassLoader();
        final ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(pluginClassLoader);
        Drapuria.init(this, server, pluginDirectory);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);


    }

    @Subscribe(async = false, order = PostOrder.LAST)
    public void onDisable(ProxyShutdownEvent event) {
        Drapuria.shutdown();
    }

    public ClassLoader getPluginClassLoader() {
        return Drapuria.class.getClassLoader();
    }
}
