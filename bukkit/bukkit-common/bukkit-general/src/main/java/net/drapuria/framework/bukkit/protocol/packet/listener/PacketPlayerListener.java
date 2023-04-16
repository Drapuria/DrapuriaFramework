package net.drapuria.framework.bukkit.protocol.packet.listener;

import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.protocol.packet.PacketService;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

@Component
public class PacketPlayerListener implements Listener {

    @Autowired
    private PacketService packetService;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (BukkitUtil.isNPC(player)) {
            return;
        }
        this.packetService.inject(player);
    }

}
