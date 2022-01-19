package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.factory.MenuFactory;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
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
                new MenuFactory()
                        .title(HumanEntity::getName)
                        .size(player1 -> 54)
                        .button(5, player1 -> new Button() {
                            @Override
                            public ItemStack getIcon(Player player) {
                                return ItemBuilder.of(Material.BLACKSTONE)
                                        .setDisplayName("§a§lBLACKSTONE").build();
                            }

                            @Override
                            public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {
                                playSuccess(player);
                                player.sendMessage("CLICK");
                            }
                        }).buildMenu().openMenu(player);

            }
        }.runTaskLater(Drapuria.PLUGIN, 20 * 5);

    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRepository.deleteById(event.getPlayer().getUniqueId());
    }

}
