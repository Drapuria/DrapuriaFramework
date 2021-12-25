package net.drapuria.framework.module.service;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.repository.ModuleRepository;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.services.PostInitialize;
import net.drapuria.framework.services.PreInitialize;
import net.drapuria.framework.services.Service;
import net.drapuria.framework.util.Stacktrace;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;

@Service(name = "Modules")
public class ModuleService {

    public static ModuleService getInstance;

    private final Map<AbstractPlugin, ModuleRepository> repositories = new HashMap<>();
    private final File dataFolder = new File(DrapuriaCommon.PLATFORM.getDataFolder(), "modules");
    private final Logger logger = DrapuriaCommon.getLogger();

    @PreInitialize
    public void preInit() {
        getInstance = this;
    }

    @PostInitialize
    public void init() {
        if (!dataFolder.exists())
            dataFolder.mkdir();

        loadInternalModules();
    }

    private void loadInternalModules() {
        loadModules(dataFolder, null, true);
    }

    public void loadModules(File base, ModuleParent<?> parent) {
        try {
            loadModules(base, parent, true);
        } catch (Exception e) {
            Stacktrace.print(e);
        }
    }

    private void loadModules(File base, ModuleParent<?> parent, boolean log) {
        if (!base.exists()) {
            base.mkdir();
        }
        if (log)
            logger.info("[Drapuria-Modules] Searching for modules in ");
        final File[] possibleModules = Arrays.stream(Objects.requireNonNull(base.listFiles(pathname -> pathname.getName().endsWith(".jar"))))
                .filter(this::isModule).toArray(File[]::new);
        final int moduleCount = Arrays.stream(possibleModules).filter(this::isModule).toArray().length;
        if (log)
            logger.info("[Drapuria-Modules] " + moduleCount + " modules found.");

        for (File file : possibleModules) {
            ModuleClassLoader classLoader = parent.createModuleClassLoader();
        }
    }

    private boolean isModule(final File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            return false;
        }
        boolean isModule = jarFile.stream().anyMatch(entry -> {
            ModuleData data;
            try {
                data = Class.forName(entry.getName().replace(".class", "")
                                .replace("/", "."),
                        false, new URLClassLoader(new URL[]{file.toURI().toURL()},
                                this.getClass().getClassLoader()))
                        .getAnnotation(ModuleData.class);
            } catch (ClassNotFoundException | MalformedURLException e) {
                return false;
            }
            return data != null;
        });
        try {
            jarFile.close();
        } catch (IOException ignored) { }
        return isModule;
    }

}
