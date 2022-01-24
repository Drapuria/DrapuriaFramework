/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.drapuria.framework.bukkit.protocol.ProtocolImplementation;

public class ProtocolLibImplementation implements ProtocolImplementation<ProtocolManager> {

    private final ProtocolManager protocolManager;

    public ProtocolLibImplementation() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public ProtocolManager get() {
        return this.protocolManager;
    }
}
