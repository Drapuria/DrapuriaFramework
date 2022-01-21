/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.module.loader;

import net.drapuria.framework.bukkit.impl.module.reflection.ModuleReflectionHelper;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.service.ModuleService;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitModuleClassLoader extends ModuleClassLoader {

    public BukkitModuleClassLoader(ModuleParent<?> moduleParent, File moduleFile, ModuleService moduleService, ClassLoader parent) throws MalformedURLException {
        super(moduleParent, moduleFile, moduleService, parent);
    }

    @Override
    public void onClassLoaded(String className, Class<?> clazz) {
        Map<PluginLoader, ConcurrentHashMap<String, Class<?>>> list = ModuleReflectionHelper.getJavaPluginLoaderClasses();
        list.values().forEach(m -> m.put(className, clazz));
        ConcurrentHashMap<String, Class<?>> classMap = ModuleReflectionHelper.getPluginClassLoaderClasses();
        classMap.put(className, clazz);
        ModuleReflectionHelper.setPluginClassLoaderClasses(classMap);
        ModuleReflectionHelper.setJavaPluginLoaderClasses(list);
    }

    @Override
    public void onClassUnload(String className, Class<?> clazz) {
        Map<PluginLoader, ConcurrentHashMap<String, Class<?>>> list = ModuleReflectionHelper.getJavaPluginLoaderClasses();
        list.values().forEach(m -> m.remove(className, clazz));
        ConcurrentHashMap<String, Class<?>> classMap = ModuleReflectionHelper.getPluginClassLoaderClasses();
        classMap.remove(className, clazz);
        ModuleReflectionHelper.setPluginClassLoaderClasses(classMap);
        ModuleReflectionHelper.setJavaPluginLoaderClasses(list);
    }
}