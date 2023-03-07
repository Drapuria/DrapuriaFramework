/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.inventory.menu.*;
import net.drapuria.framework.bukkit.inventory.menu.factory.MenuFactory;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.language.LanguageService;
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
    private final LanguageService languageService = LanguageService.getService;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final DrapuriaPlayer drapuriaPlayer = new DrapuriaPlayer1_8(player, languageService.getDefaultLocale());
        playerRepository.save(drapuriaPlayer);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRepository.deleteById(event.getPlayer().getUniqueId());
    }

}
