/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.listener.FilteredListener;
import net.drapuria.framework.beans.component.ComponentHolder;
import org.bukkit.event.Listener;

public class ComponentHolderBukkitListener extends ComponentHolder {

    @Override
    public Object newInstance(Class<?> type) {
        try {
            Object object =  this.newInstance(this.constructorDetails(type));
            if (FilteredListener.class.isAssignableFrom(type)) {
                return object;
            } else if (Listener.class.isAssignableFrom(type)) {
                Drapuria.registerEvents((Listener) object);
            } else {
                Drapuria.LOGGER.error("The Class " + type.getSimpleName() + " wasn't implement Listener or FunctionListener!");
                return null;
            }
            return object;

        } catch (Exception ignored) {
            return null;
        }
    }



    @Override
    public Class<?>[] type() {
        return new Class[] {FilteredListener.class, Listener.class};
    }
}
