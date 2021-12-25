package net.drapuria.framework.bukkit.events;

import net.drapuria.framework.bukkit.Drapuria;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void call() {
        Drapuria.PLUGIN.getServer().getPluginManager().callEvent(this);
    }

}
