package net.drapuria.framework.bungee.impl;

import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.bungee.Drapuria;
import net.drapuria.framework.bungee.plugin.PluginProvider;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class ComponentHolderBungeeListener extends ComponentHolder {
    @Override
    public Class<?>[] type() {
        return new Class[] {Listener.class};
    }

    @Override
    public Object newInstance(Class<?> type) {
        Drapuria.LOGGER.info("Loading listener " + type.getSimpleName());
        Plugin plugin = Drapuria.PLUGIN;
        Class<? extends Plugin> pluginClass = Plugin.class;
        PluginProvider provider = type.getAnnotation(PluginProvider.class);
        if (provider != null) {
            pluginClass = provider.value();
            plugin = getPlugin(pluginClass);
        }
        Listener listener;
        constructor: {
            try {
                listener = (Listener) type.getConstructor(pluginClass)
                        .newInstance(plugin);
                break constructor;
            } catch (ReflectiveOperationException e) {
                Drapuria.LOGGER.error("Failed to load listener " + type.getSimpleName() + " with " + pluginClass);
            }
            try {
                listener = (Listener) type.newInstance();
                break constructor;
            } catch (ReflectiveOperationException e) {
                Drapuria.LOGGER.error("Failed to load listener" + type.getSimpleName());
            }
            throw new RuntimeException("Could not find valid constructor for " + type.getSimpleName());
        }
        try {
            Drapuria.getProxy().getPluginManager().registerListener(plugin, listener);
        } catch (Exception ignored) {

        }
        return listener;
    }

    @Nullable
    public static <T extends Plugin> T getPlugin(Class<T> type) {
        for (Plugin plugin : Drapuria.getProxy().getPluginManager().getPlugins()) {
            if (type.isInstance(plugin)) {
                return type.cast(plugin);
            }
        }

        return null;
    }

}
