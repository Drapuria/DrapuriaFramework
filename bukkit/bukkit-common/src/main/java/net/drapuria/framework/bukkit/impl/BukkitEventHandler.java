package net.drapuria.framework.bukkit.impl;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.events.PostServicesInitialEvent;
import net.drapuria.framework.events.IEventHandler;

public class BukkitEventHandler implements IEventHandler {
    @Override
    public void onPostServicesInitial() {
        Drapuria.callEvent(new PostServicesInitialEvent());
    }
}
