package net.drapuria.framework.velocity.plugin;

import com.velocitypowered.api.proxy.ProxyServer;
import net.drapuria.framework.plugin.ClassPathAppender;

import java.nio.file.Path;

public class VelocityClassPathAppender implements ClassPathAppender {

    private final ProxyServer server;
    private final Object plugin;

    public VelocityClassPathAppender(ProxyServer server, Object plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void addJarToClassPath(Path path) {
        System.out.println("add to velocity class path");
        this.server.getPluginManager().addToClasspath(this.plugin, path);
    }
}
