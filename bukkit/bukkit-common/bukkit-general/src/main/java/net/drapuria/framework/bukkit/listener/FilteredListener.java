/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.listener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.*;
import org.bukkit.plugin.*;

@NoArgsConstructor
public class FilteredListener<T extends Plugin> implements Listener {

    @Getter
    private FilteredEventList eventList;
    public T plugin;

    public FilteredListener(FilteredEventList eventList, T plugin) {
        this.initial(plugin, eventList);
    }

    public void initial(T plugin, FilteredEventList eventChecker) {
        this.plugin = plugin;
        this.eventList = eventChecker;
        FilteredListenerRegistry.INSTANCE.register(this);
    }

}
