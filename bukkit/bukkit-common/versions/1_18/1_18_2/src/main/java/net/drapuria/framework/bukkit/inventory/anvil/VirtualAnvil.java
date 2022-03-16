/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.anvil;

import net.drapuria.framework.bukkit.Drapuria;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;


public abstract class VirtualAnvil extends AbstractVirtualAnvil {

    protected Player player;
    private Inventory anvilInv;

    private static Map<Player, VirtualAnvil> managedAnvils;

    static {
        managedAnvils = new HashMap<>();
    }

    public VirtualAnvil(Player player) {
        this(player, " ");
    }

    public VirtualAnvil(Player player, String startText) {
        this(player, startText, new ItemStack(Material.PAPER));
    }

    public VirtualAnvil(Player player, String startText, ItemStack displayItem) {
        managedAnvils.remove(player);
        this.player = player;
        if (startText == null || startText.equals(""))
            startText = " ";
        ItemMeta meta = displayItem.getItemMeta();
        meta.displayName(Component.text(startText));
        displayItem.setItemMeta(meta);
        anvilInv = openVirtualAnvil(player, startText, displayItem);
        managedAnvils.put(player, this);
    }

    @Override
    public Inventory getAnvilInventory() {
        return this.anvilInv;
    }

    @Override
    public void openAnvil() {
        // unused
    }

    @AnvilImpl
    public static void onEnable() {
        Bukkit.getPluginManager().registerEvents(new AnvilEventHandler(), Drapuria.PLUGIN);
    }

    public static Map<Player, VirtualAnvil> getManagedAnvils() {
        return managedAnvils;
    }

    public static Inventory openVirtualAnvil(Player p, String title, ItemStack itemStack) {
        Location loc = p.getLocation();
        CraftPlayer craftPlayer = (CraftPlayer) p;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();
        AnvilContainer container = new AnvilContainer(p, craftPlayer.getHandle().nextContainerCounter(), title);
        container.getBukkitView().getTopInventory().setItem(0, itemStack);
        entityPlayer.b.a(new PacketPlayOutOpenWindow(container.getContainerId(), Containers.h, new ChatComponentText(title)));
        entityPlayer.a((Container) container);
        setActiveContainer(p, container);
        return container.getBukkitView().getTopInventory();
    }

    public static void resetActiveContainer(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.bV = entityPlayer.bU;
    }

    public static void setActiveContainer(Player player, Object container) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.bV = (Container) container;
    }

    private static class AnvilEventHandler implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onClick(InventoryClickEvent e) {
            if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.ANVIL
                    || !(e.getWhoClicked() instanceof Player p))
                return;
            if (!(managedAnvils.containsKey(p)))
                return;
            e.setCancelled(true);
            if (e.getRawSlot() == 2) {
                VirtualAnvil anvil = managedAnvils.get(p);
                String text = null;
                if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()
                        && e.getCurrentItem().getItemMeta().hasDisplayName()
                        && !(e.getCurrentItem().getItemMeta().getDisplayName().equals("")))
                    text = e.getCurrentItem().getItemMeta().getDisplayName();
                anvil.onConfirm(text);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onClose(InventoryCloseEvent e) {
            if (e.getInventory().getType() != InventoryType.ANVIL
                    || !(e.getPlayer() instanceof Player p))
                return;
            if (managedAnvils.containsKey(p)) {
                VirtualAnvil anvil = managedAnvils.get(p);
                managedAnvils.remove(p);
                anvil.getAnvilInventory().clear();
                p.getOpenInventory().setCursor(new ItemStack(Material.AIR));
                anvil.onCancel();
                resetActiveContainer(p);
            }
        }
    }
}
