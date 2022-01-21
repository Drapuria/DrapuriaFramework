/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.velocity.impl;

import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.velocity.Drapuria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class VelocityDrapuriaPlatform implements DrapuriaPlatform {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void saveResources(String resourcePath, boolean replace) {
        File dataFolder = getDataFolder();
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in Mitw.jar");
            } else {
                File outFile = new File(dataFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        FrameworkMisc.PLATFORM.getLogger().warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    FrameworkMisc.PLATFORM.getLogger().error("Could not save " + outFile.getName() + " to " + outFile);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    private InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        } else {
            try {
                URL url = Drapuria.PLUGIN.getClass().getClassLoader().getResource(filename);
                if (url == null) {
                    return null;
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } catch (IOException var4) {
                return null;
            }
        }
    }

    @Override
    public PluginClassLoader getClassLoader() {
        return Drapuria.CLASS_LOADER;
    }

    @Override
    public File getDataFolder() {
        return Drapuria.PLUGIN_DIRECTORY.toFile();
    }

    @Override
    public Logger getLogger() {
        return LogManager.getLogger();
    }

    @Override
    public void shutdown() {
        Drapuria.SERVER.shutdown();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public boolean isServerThread() {
        return false;
    }

    @Override
    public void registerCommandProvider() {

    }
}
