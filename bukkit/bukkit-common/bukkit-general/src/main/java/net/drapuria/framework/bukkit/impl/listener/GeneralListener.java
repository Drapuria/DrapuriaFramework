/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.listener;

import net.drapuria.framework.bukkit.item.skull.impl.HDBRepository;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Component
public class GeneralListener implements Listener {

    @Autowired
    private HDBRepository hdbRepository;

    @EventHandler
    public void onHDBLoad(final DatabaseLoadEvent event) {
        hdbRepository.processQueuedIds();
    }
}
