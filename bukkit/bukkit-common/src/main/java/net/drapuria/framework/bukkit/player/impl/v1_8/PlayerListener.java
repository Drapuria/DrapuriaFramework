package net.drapuria.framework.bukkit.player.impl.v1_8;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

@Component // NEEDS TO BE DONE MANUALLY LATER ON
public class PlayerListener implements Listener {

    private final PlayerRepository playerRepository = PlayerRepository.getRepository;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final DrapuriaPlayer<ItemStack> drapuriaPlayer = new DrapuriaPlayer1_8(player);
        playerRepository.save(drapuriaPlayer);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRepository.deleteById(event.getPlayer().getUniqueId());
    }

}
