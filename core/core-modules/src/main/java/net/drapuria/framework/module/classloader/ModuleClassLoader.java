/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module.classloader;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.module.JavaModule;
import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.service.ModuleService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class ModuleClassLoader extends URLClassLoader {

    // Spigot Start
    static
    {
        try
        {
            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod( "registerAsParallelCapable" );
            if (method != null )
            {
                boolean oldAccessible = method.isAccessible();
                method.setAccessible( true );
                method.invoke( null );
                method.setAccessible( oldAccessible );
                DrapuriaCommon.PLATFORM.getLogger().info( "Set ModuleClassLoader as parallel capable" );
            }
        } catch ( NoSuchMethodException ex )
        {
            // Ignore
        } catch ( Exception ex )
        {
            DrapuriaCommon.PLATFORM.getLogger().info(  "Error setting ModuleClassLoader as parallel capable", ex );
        }
    }
    // Spigot End

    private final ModuleService moduleService;
    private final ModuleParent<?> moduleParent;
    private final File moduleFile;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    private ModuleAdapter moduleAdapter;


    public ModuleClassLoader(final ModuleParent<?> moduleParent, File moduleFile, ModuleService moduleService, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{moduleFile.toURI().toURL()}, parent);
        this.moduleService = moduleService;
        this.moduleParent = moduleParent;
        this.moduleFile = moduleFile;
    }

    public ModuleAdapter load() {
        JarFile jarFile = null;

        try {
            jarFile = new JarFile(moduleFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jarFile == null) {
            DrapuriaCommon.PLATFORM.getLogger().error("[Drapuria-Modules] Jar file for " + moduleFile.getName() + " is null");
            return null;
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().endsWith(".class"))
                continue;
            final String className = entry.getName()
                    .replace("/", ".")
                    .replace(".class", "");
            String[] splitClassName = className.split("\\.}");
            try {
                loadClass(className, true);
                Class<?> clazz = Class.forName(className, false, this);
                classes.put(className, clazz);
                onClassLoaded(className, clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Class<?> clazz = jarFile.stream()
                .filter(entry -> entry.getName().endsWith(".class")).filter(entry -> {
                    try {
                        return Class.forName(entry.getName()
                                        .replace(".class", "").replace("/", "."),
                                false,
                                this)
                                .getAnnotation(ModuleData.class) != null;
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                }).map(entry -> {
                    try {
                        return Class.forName(entry.getName().replace(".class", "")
                                        .replace("/", "."), false,
                                this);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }).findFirst().orElse(null);

        Class<? extends Module> moduleClass = null;
        try {
            moduleClass = clazz.asSubclass(Module.class);
            if (findLoadedClass(moduleClass.getName()) == null) {
                try {
                    loadClass(moduleClass.getName());
                } catch (ClassNotFoundException ignored) {

                }
            }
        } catch (ClassCastException ignored) {
        }

        if (moduleClass == null) {
            DrapuriaCommon.PLATFORM.getLogger().error("[Drapuria-Modules] No Module class found for " + this.moduleFile.getName());
            return null;
        }

        ModuleData data = moduleClass.getAnnotation(ModuleData.class);
        if (data == null) {
            DrapuriaCommon.PLATFORM.getLogger().error("[Drapuria-Modules] No ModuleData found for module " + this.moduleFile.getName());
            return null;
        }



        Module module = null;
        try {
            module = moduleClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ignored) {

        }
        if (module == null) {
            DrapuriaCommon.PLATFORM.getLogger().error("[Drapuria-Modules] Could not create new instance of " + moduleClass);
            return null;
        }
        this.moduleAdapter = new ModuleAdapter(data.name(), moduleParent.createModule(module, data), data, this);
        this.initialize((JavaModule) this.moduleAdapter.getModule());
        return this.moduleAdapter;
    }

    public synchronized void initialize(final JavaModule module) {
        final ModuleData moduleData = this.moduleAdapter.getModuleData();
        module.init(moduleFile, moduleData.name(), moduleData.author(), moduleData.version(), moduleData.description(), this);
    }

    public void unload() {
        this.classes.forEach(this::onClassUnload);
        this.classes.clear();
    }

    public ModuleParent<?> getModuleParent() {
        return moduleParent;
    }

    public abstract void onClassLoaded(String className, Class<?> clazz);

    public abstract void onClassUnload(String className, Class<?> clazz);

}
