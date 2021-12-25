package net.drapuria.framework.bukkit.util;

import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.Drapuria;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
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

public abstract class VirtualAnvil {

    private Player player;
    private Inventory anvilInv;
    private boolean confirmedSuccessfully = false;

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
        Inventory inv = anvilInv = openVirtualAnvil(player);
        ItemMeta meta = displayItem.getItemMeta();
        if (startText == null || startText.equals(""))
            startText = " ";
        meta.setDisplayName(startText);
        displayItem.setItemMeta(meta);
        inv.setItem(0, displayItem);
        managedAnvils.put(player, this);
    }

    public Inventory getAnvilInv() {
        return anvilInv;
    }

    public boolean isConfirmedSuccessfully() {
        return confirmedSuccessfully;
    }

    public void setConfirmedSuccessfully(boolean confirmedSuccessfully) {
        this.confirmedSuccessfully = confirmedSuccessfully;
    }

    public abstract void onConfirm(String text);

    public abstract void onCancel();

    public static void onEnable() {
        Bukkit.getPluginManager().registerEvents(new AnvilEventHandler(), Drapuria.PLUGIN);
    }

    public static Map<Player, VirtualAnvil> getManagedAnvils() {
        return managedAnvils;
    }

    @SneakyThrows
    public static Inventory openVirtualAnvil(Player p) {
        Location loc = p.getLocation();
        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
        Container container = CraftEventFactory.callInventoryOpenEvent(entityPlayer, new ContainerAnvil(entityPlayer.inventory, entityPlayer.world,
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), entityPlayer));
        entityPlayer.nextContainerCounter();
        int containerCounter = (int) ReflectionUtils.getValue(entityPlayer, true,"containerCounter");
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, "minecraft:anvil", new ChatMessage("Repairing", new Object[0]), 0));
        container.checkReachable = false;
        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.windowId = containerCounter;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        return container.getBukkitView().getTopInventory();
    }

    private static class AnvilEventHandler implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onClick(InventoryClickEvent e) {
            if (e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.ANVIL
                    || !(e.getWhoClicked() instanceof Player))
                return;
            Player p = (Player) e.getWhoClicked();
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
                    || !(e.getPlayer() instanceof Player))
                return;
            Player p = (Player) e.getPlayer();
            if (managedAnvils.containsKey(p)) {
                VirtualAnvil anvil = managedAnvils.get(p);
                managedAnvils.remove(p);
                anvil.getAnvilInv().clear();
                p.getOpenInventory().setCursor(new ItemStack(Material.AIR));
                anvil.onCancel();
            }
        }

    }

}
