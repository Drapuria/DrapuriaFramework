/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module.classloader;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.service.ModuleService;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlatformBasedModuleClassLoader extends ModuleClassLoader {

    private static boolean isBukkit;

    private static Method methodGetJavaPluginLoaderClasses;
    private static Method methodGetPluginClassLoaderClasses;
    private static Method methodSetPluginClassLoaderClasses;
    private static Method methodSetJavaPluginLoaderClasses;

    static {
        try {
            Class.forName("org.bukkit.plugin.SimplePluginManager");
            isBukkit = true;
        } catch (ClassNotFoundException e) {
            isBukkit = false;
        }
        try {
            Class<?> moduleReflectionHelper = Class.forName("net.drapuria.framework.bukkit.impl.module.reflection.ModuleReflectionHelper");
            methodGetJavaPluginLoaderClasses = moduleReflectionHelper.getMethod("getJavaPluginLoaderClasses");
            methodGetPluginClassLoaderClasses = moduleReflectionHelper.getMethod("getPluginClassLoaderClasses");
            methodSetPluginClassLoaderClasses = moduleReflectionHelper.getMethod("setPluginClassLoaderClasses",
                    ConcurrentHashMap.class);
            methodSetJavaPluginLoaderClasses = moduleReflectionHelper.getMethod("setJavaPluginLoaderClasses",
                    Map.class);

            methodSetPluginClassLoaderClasses.setAccessible(true);
            methodSetJavaPluginLoaderClasses.setAccessible(true);
            methodGetPluginClassLoaderClasses.setAccessible(true);
            methodGetJavaPluginLoaderClasses.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public PlatformBasedModuleClassLoader(ModuleParent<?> moduleParent,
                                          File moduleFile,
                                          ModuleService moduleService,
                                          ClassLoader parent) throws MalformedURLException {
        super(moduleParent, moduleFile, moduleService, parent);
    }

    @Override
    public void onClassLoaded(String className, Class<?> clazz) {
        if (isBukkit) {
            try {
                Map<Object, ConcurrentHashMap<String, Class<?>>> list = getJavaPluginClassLoaderClasses();
                ConcurrentHashMap<String, Class<?>> classMap = getPluginClassLoaderClasses();
                list.values().forEach(map -> map.put(className, clazz));
                classMap.put(className, clazz);
                methodSetPluginClassLoaderClasses.invoke(null, classMap);
                methodSetJavaPluginLoaderClasses.invoke(null, list);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClassUnload(String className, Class<?> clazz) {
        if (isBukkit) {
            try {
                Map<Object, ConcurrentHashMap<String, Class<?>>> list = getJavaPluginClassLoaderClasses();
                ConcurrentHashMap<String, Class<?>> classMap = getPluginClassLoaderClasses();
                list.values().forEach(map -> map.remove(className, clazz));
                classMap.remove(className, clazz);
                methodSetPluginClassLoaderClasses.invoke(null, classMap);
                methodSetJavaPluginLoaderClasses.invoke(null, list);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private Map<Object, ConcurrentHashMap<String, Class<?>>> getJavaPluginClassLoaderClasses() {
        return (Map<Object, ConcurrentHashMap<String, Class<?>>>) methodGetJavaPluginLoaderClasses.invoke(null);
    }

    @SneakyThrows
    private ConcurrentHashMap<String, Class<?>> getPluginClassLoaderClasses() {
        return (ConcurrentHashMap<String, Class<?>>) methodGetPluginClassLoaderClasses.invoke(null);
    }

}
