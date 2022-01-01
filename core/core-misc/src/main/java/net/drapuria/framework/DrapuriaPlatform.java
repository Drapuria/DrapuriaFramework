package net.drapuria.framework;

import net.drapuria.framework.plugin.PluginClassLoader;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;

public interface DrapuriaPlatform {

    void saveResources(String name, boolean replace);

    PluginClassLoader getClassLoader();

    File getDataFolder();

    Logger getLogger();

    default @Nullable
    String identifyClassLoader(ClassLoader classLoader) throws Exception {
        return null;
    }

    void shutdown();

    boolean isShuttingDown();

    boolean isServerThread();

    void registerCommandProvider();
}
