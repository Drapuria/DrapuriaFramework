package net.drapuria.framework.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PostServicesInitialEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
