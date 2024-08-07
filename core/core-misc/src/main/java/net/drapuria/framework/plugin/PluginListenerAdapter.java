/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.plugin;

public interface PluginListenerAdapter {

    default void onPluginInitiate(AbstractPlugin plugin) {

    }

    default void onPluginEnable(AbstractPlugin plugin) {

    }

    default void onPluginDisable(AbstractPlugin plugin) {

    }

    default void onPluginPostEnable(AbstractPlugin plugin) {

    }

    /**
     * @return The priority of the listener
     */
    default int priority() {
        return 0;
    }

}
