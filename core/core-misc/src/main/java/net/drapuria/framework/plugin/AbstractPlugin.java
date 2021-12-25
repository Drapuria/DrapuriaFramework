package net.drapuria.framework.plugin;

public interface AbstractPlugin {

    default void onInitial() {

    }

    default void onPreEnable() {

    }

    default void onPluginEnable() {

    }

    default void onPluginDisable() {

    }

    default void onFrameworkFullyDisable() {

    }

    void close();

    ClassLoader getPluginClassLoader();

    String getName();

}
