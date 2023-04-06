package net.drapuria.framework.beans.configuration;


import org.jetbrains.annotations.Nullable;

/**
 * Provides all the necessary methods for a Configuration Class which implements a Configuration
 *
 * @param <T> The Interface this provider handles
 */
public abstract class AbstractConfigurationProvider<T> {


    public abstract void onEnable(T config);

    public abstract void onPostEnable(T config);

    public void internalOnEnable(@Nullable Object instance) {
        onEnable((T) instance);
    }

    public void internalOnPostEnable(@Nullable Object instance) {
        onPostEnable((T) instance);
    }
}