/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.plugin;


import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PluginManager {

    public static PluginManager INSTANCE;

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    public static void initialize(PluginHandler pluginHandler) {
        if (INSTANCE != null) {
            throw new IllegalArgumentException("Don't Initialize twice!");
        }

        INSTANCE = new PluginManager(pluginHandler);
    }

    private final Map<String, AbstractPlugin> plugins;
    private final Set<PluginListenerAdapter> listenerAdapters;
    private final PluginHandler pluginHandler;

    public PluginManager(PluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;

        this.plugins = new ConcurrentHashMap<>();
        this.listenerAdapters = new TreeSet<>(Collections.reverseOrder(Comparator.comparingInt(PluginListenerAdapter::priority)));
    }

    public Collection<ClassLoader> getClassLoaders() {
        return this.plugins.values()
                .stream()
                .map(AbstractPlugin::getPluginClassLoader)
                .collect(Collectors.toList());
    }

    public void onPluginInitial(AbstractPlugin plugin) {
        synchronized (this.listenerAdapters) {
            this.listenerAdapters.forEach(listenerAdapter -> listenerAdapter.onPluginInitiate(plugin));
        }
    }

    public void onPluginEnable(AbstractPlugin plugin) {
        synchronized (this.listenerAdapters) {
            this.listenerAdapters.forEach(listenerAdapter -> listenerAdapter.onPluginEnable(plugin));
        }
    }

    public void onPluginPostEnable(AbstractPlugin plugin) {
        synchronized (this.listenerAdapters) {
            this.listenerAdapters.forEach(listenerAdapter -> listenerAdapter.onPluginPostEnable(plugin));
        }
    }

    public void onPluginDisable(AbstractPlugin plugin) {
        synchronized (this.listenerAdapters) {
            this.listenerAdapters.forEach(listenerAdapter -> listenerAdapter.onPluginDisable(plugin));
        }
    }

    public Collection<AbstractPlugin> getPlugins() {
        return this.plugins.values();
    }

    public AbstractPlugin getPlugin(String name) {
        return this.plugins.get(name);
    }

    public void addPlugin(AbstractPlugin plugin) {
        this.plugins.put(plugin.getName(), plugin);
    }

    public void callFrameworkFullyDisable() {
        this.plugins.values().forEach(AbstractPlugin::onFrameworkFullyDisable);
    }

    public void registerListener(PluginListenerAdapter listenerAdapter) {
        synchronized (this.listenerAdapters) {
            this.listenerAdapters.add(listenerAdapter);
        }
    }

    @Nullable
    public AbstractPlugin getPluginByClass(Class<?> type) {
        String name = this.pluginHandler.getPluginByClass(type);
        if (name == null) {
            return null;
        }

        return this.getPlugin(name);
    }

}
