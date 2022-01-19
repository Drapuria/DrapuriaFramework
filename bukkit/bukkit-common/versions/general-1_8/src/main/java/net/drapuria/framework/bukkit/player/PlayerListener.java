package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.inventory.menu.*;
import net.drapuria.framework.bukkit.inventory.menu.factory.MenuFactory;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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
import java.util.concurrent.atomic.AtomicReference;

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
                /*
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
                 */
           //     drapuriaPlayer.teleportAsync(new Location(player.getWorld(), -30000, 15, -3000));
                final AtomicReference<IMenu> menu = new AtomicReference<>();
                final AtomicReference<IMenu> test = new AtomicReference<>();
                menu.set(new MenuFactory().inventoryType(player1 -> InventoryType.HOPPER)
                        .button(0, player1 -> new Button() {
                            @Override
                            public ItemStack getIcon(Player player) {
                                return ItemBuilder.of(Material.CHEST).setDisplayName("§aHey")
                                        .build();
                            }

                            @Override
                            public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {
                                playNeutral(player);
                                test.get().openMenu(player);
                            }
                        })
                        .title(player1 -> "§a" + player1.getName())
                        .buildMenu());
                test.set(new MenuFactory().size(player1 -> 27)
                        .title(HumanEntity::getName)
                        .button(5, player1 -> new Button() {
                            @Override
                            public ItemStack getIcon(Player player) {
                                return ItemBuilder.of(Material.ENDER_CHEST)
                                        .setDisplayName("§bYOYO")
                                        .build();
                            }

                            @Override
                            public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {
                                playSuccess(player);
                                menu.get().openMenu(player);
                            }
                        }).buildMenu());
                test.get().openMenu(player);
            }
        }.runTaskLaterAsynchronously(Drapuria.PLUGIN, 20 * 10);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRepository.deleteById(event.getPlayer().getUniqueId());
    }

}
