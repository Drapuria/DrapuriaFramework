package net.drapuria.framework.bukkit.protocol.packet.event.type;

import net.drapuria.framework.bukkit.protocol.packet.event.PacketEvent;
import org.bukkit.entity.Player;

/**
 * This event is called each time you inject a player.
 */
public final class PlayerInjectEvent extends PacketEvent {
    private final Player player;

    public PlayerInjectEvent(final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
