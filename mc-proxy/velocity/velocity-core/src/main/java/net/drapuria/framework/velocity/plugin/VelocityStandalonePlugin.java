package net.drapuria.framework.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.velocity.Drapuria;

import java.nio.file.Path;

@Plugin(id = "drapuriaframework", name = "Drapuria Framework", version = "${project.version}",
        url = "http://drapuria.net", description = "Drapuria Framework", authors = "Drapuria Development Team")
public class VelocityStandalonePlugin extends VelocityPlugin {

    private final ProxyServer server;
    private final Path pluginDirectory;
    @Inject
    public VelocityStandalonePlugin(ProxyServer server, @DataDirectory Path dataDirectory) {
        super(server);
        this.pluginDirectory = dataDirectory;
        this.server = server;
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        Drapuria.init(this, this.server, pluginDirectory);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getPluginClassLoader());
        this.onPluginDisable();

        PluginManager.INSTANCE.onPluginDisable(this);
        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return super.getPluginClassLoader();
    }
}
