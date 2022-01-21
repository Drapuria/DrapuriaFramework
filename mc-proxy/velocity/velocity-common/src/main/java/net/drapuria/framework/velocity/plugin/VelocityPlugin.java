/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.plugin.PluginManager;

public class VelocityPlugin implements AbstractPlugin {

    private final ProxyServer server;

    @Inject
    public VelocityPlugin(ProxyServer server) {
        this.server = server;
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getPluginClassLoader());
        PluginManager.INSTANCE.addPlugin(this);
        PluginManager.INSTANCE.onPluginInitial(this);
        this.onInitial();
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public void close() {

    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
