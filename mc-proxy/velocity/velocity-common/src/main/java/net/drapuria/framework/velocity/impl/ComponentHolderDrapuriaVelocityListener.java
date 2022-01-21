/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.velocity.impl;

import com.velocitypowered.api.proxy.ProxyServer;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.velocity.Drapuria;
import net.drapuria.framework.velocity.listener.Listener;


public class ComponentHolderDrapuriaVelocityListener extends ComponentHolder {

    private final ProxyServer server;

    public ComponentHolderDrapuriaVelocityListener(ProxyServer server) {
        this.server = server;
    }

    @Override
    public Class<?>[] type() {
        return new Class[] {Listener.class};
    }


    @Override
    public Object newInstance(Class<?> type) {
        Object listener = super.newInstance(type);
        server.getEventManager().register(Drapuria.PLUGIN, listener);
        return listener;
    }
}
