package net.drapuria.framework.bukkit.impl.module.reflection;

import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ModuleReflectionHelper {

    public static SimplePluginManager getSimplePluginManager() {
        return (SimplePluginManager) Bukkit.getPluginManager();
    }


    @SneakyThrows
    public static HashMap<Pattern, PluginLoader> getFileAssociations() {
        return (HashMap<Pattern, PluginLoader>) ReflectionUtils.getValue(getSimplePluginManager(), true, "fileAssociations");
    }


    @SneakyThrows
    public static PluginClassLoader getPluginClassLoader() {
        PluginLoader pluginLoader = getFileAssociations().values().stream().findFirst().orElse(null);
        LinkedHashMap<String, PluginClassLoader> map = (LinkedHashMap<String, PluginClassLoader>) ReflectionUtils.getValue(pluginLoader, true, "loaders");
        return map.get(Drapuria.PLUGIN.getName());
    }



    @SneakyThrows
    public static ConcurrentHashMap<String, Class<?>> getPluginClassLoaderClasses() {
        ConcurrentHashMap<String, Class<?>> map = (ConcurrentHashMap<String, Class<?>>) ReflectionUtils.getValue(getPluginClassLoader(), true,"classes");
        return map;
    }

    @SneakyThrows
    public static void setPluginClassLoaderClasses(ConcurrentHashMap<String, Class<?>> classes) {
        PluginClassLoader classLoader = getPluginClassLoader();
        Field field = ReflectionUtils.getField(PluginClassLoader.class, true,"classes");
        field.setAccessible(true);
        try {
            field.set(classLoader, classes);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void setJavaPluginLoaderClasses(Map<PluginLoader, ConcurrentHashMap<String, Class<?>>> map) {
        for (PluginLoader loader : map.keySet()) {
            Field field = ReflectionUtils.getField(JavaPluginLoader.class,true, "classes");
            field.setAccessible(true);
            try {
                field.set(loader, map.get(loader));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public static Map<PluginLoader, ConcurrentHashMap<String, Class<?>>> getJavaPluginLoaderClasses() {
        Map<PluginLoader, ConcurrentHashMap<String, Class<?>>> list = new HashMap<>();
        Collection<PluginLoader> loaders = getFileAssociations().values();

        for (PluginLoader loader : loaders) {
            ConcurrentHashMap<String, Class<?>> m =  (ConcurrentHashMap<String, Class<?>>) ReflectionUtils.getValue(loader, true, "classes");
            list.put(loader, m);
        }
        return list;
    }
}
