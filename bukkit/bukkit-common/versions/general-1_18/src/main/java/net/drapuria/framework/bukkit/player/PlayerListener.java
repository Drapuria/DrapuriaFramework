package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.Drapuria;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

@Component
public class PlayerListener implements Listener {

    private final PlayerRepository playerRepository = PlayerRepository.getRepository;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final DrapuriaPlayer drapuriaPlayer = new DrapuriaPlayer1_18(player);
        playerRepository.save(drapuriaPlayer);
        new BukkitRunnable() {
            @Override
            public void run() {
                Drapuria.IMPLEMENTATION.sendActionBar(player, "§a§lHALLO");
            }
        }.runTaskLater(Drapuria.PLUGIN, 20 * 5);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRepository.deleteById(event.getPlayer().getUniqueId());
    }

}
