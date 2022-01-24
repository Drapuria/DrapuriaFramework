/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import lombok.SneakyThrows;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Service(name = "protocollib")
public class ProtocolLibService {

    public static ProtocolLibService getService;

    private boolean enabled;
    private ProtocolManager protocolManager;

    @PreInitialize
    public void onPreInit() {
        getService = this;
        try {
            Class.forName("com.comphenix.protocol.ProtocolManager");
            enabled = true;
            protocolManager = ProtocolLibrary.getProtocolManager();
        } catch (Exception ignored) {
            enabled = false;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public void sendPacket(Player player, PacketContainer packet) {
        try {
            if (!enabled) throw new RuntimeException("ProtocolLib Service is not accessible!");
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not send packet!", e);
        }
    }
}
