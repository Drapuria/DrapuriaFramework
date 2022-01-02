package net.drapuria.framework.bungee.impl;

import net.drapuria.framework.bungee.Drapuria;
import net.drapuria.framework.bungee.plugin.PluginProvider;
import net.drapuria.framework.services.ComponentHolder;
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
            }
            try {
                listener = (Listener) type.newInstance();
            } catch (ReflectiveOperationException e) {
            }
            throw new RuntimeException("Could not find valid constructor for " + type.getSimpleName());
        }
        Drapuria.getProxy().getPluginManager().registerListener(plugin, listener);
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
