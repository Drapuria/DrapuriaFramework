package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.inventory.menu.AbstractButton;
import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.SharedMenu;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerListener implements Listener {

    private final PlayerRepository playerRepository = PlayerRepository.getRepository;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final DrapuriaPlayer drapuriaPlayer = new DrapuriaPlayer1_8(player);
        playerRepository.save(drapuriaPlayer);
        new BukkitRunnable() {
            @Override
            public void run() {
                new SharedMenu() {
                    @Override
                    public Map<Integer, IButton> getButtons() {
                        Map<Integer, IButton> buttons = new HashMap<>();
                        buttons.put(1, new AbstractButton() {
                            @Override
                            public ItemStack getIcon() {
                                return ItemBuilder.of(Material.NETHER_STAR).setDisplayName("TEST")
                                        .build();
                            }

                            @Override
                            public ItemStack setIcon(ItemStack inventory) {
                                return null;
                            }

                            @Override
                            public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {
                                if (clickType == ClickType.DOUBLE_CLICK) {
                                    player.sendMessage("DOUBLE CLICK");
                                }
                                player.sendMessage("CLICK");
                            }
                        });
                        return buttons;
                    }

                    @Override
                    public String getTitle() {
                        return "HALLO";
                    }

                    @Override
                    public int getDefaultSize() {
                        return 27;
                    }

                    @Override
                    public boolean allowOwnInventoryClick() {
                        return true;
                    }
                }.openMenu(player);
            }
        }.runTaskLater(Drapuria.PLUGIN, 20 * 2);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRepository.deleteById(event.getPlayer().getUniqueId());
    }

}
