/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.bukkit.impl.annotation.ServerImpl;
import net.drapuria.framework.bukkit.inventory.anvil.AbstractVirtualAnvil;
import net.drapuria.framework.bukkit.inventory.anvil.ConfirmAction;
import net.drapuria.framework.bukkit.inventory.anvil.VirtualAnvil;
import net.drapuria.framework.bukkit.protocol.ProtocolService;
import net.drapuria.framework.bukkit.util.BlockPosition;
import net.drapuria.framework.bukkit.util.Skin;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@ServerImpl
public class ServerImplementation1_18 implements ServerImplementation {
    @Override
    public Entity getEntity(UUID uuid) { // REPLACE WITH FOR LOOP
        //noinspection Convert2streamapi ---> for loop is faster in this case since we return @ first hit
        for (World world : Bukkit.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity != null)
                return entity;
        }
        return null;
    }

    @Override
    public Entity getEntity(World world, int id) {
        return world.getEntities().stream().filter(entity -> entity.getEntityId() == id).findFirst().orElse(null);
    }

    @Override
    public Object toBlockNMS(MaterialData materialData) {
        return null;
    }

    @Override
    public List<Player> getPlayersInRadius(Location location, double radius) {
        return location.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());
    }

    @Override
    public void setFakeBlocks(Player player, Map<BlockPosition, MaterialData> positions, List<BlockPosition> toRemove, boolean send) {

    }

    @Override
    public void clearFakeBlocks(Player player, boolean send) {

    }

    @Override
    public void sendActionBar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }

    @Override
    public boolean isServerThread() {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public boolean callMoveEvent(Player player, Location from, Location to) {
        return false;
    }

    @Override
    public Skin getSkinFromPlayer(Player player) {
       return ProtocolService.protocolService.getVersionHelper().getSkinFromPlayer(player);
    }

    @Override
    public AbstractVirtualAnvil createVirtualAnvil(Player player, BiConsumer<Player, ConfirmAction> onCancel,BiFunction<Player, String, ConfirmAction> confirm) {
        return new VirtualAnvil(player) {
            @Override
            public void onConfirm(String text) {
                ConfirmAction action = confirm.apply(this.player, text);
                setConfirmAction(action);
                if (action == ConfirmAction.CONFIRMED)
                    FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
                        player.closeInventory();

                    });
            }

            @Override
            public void onCancel() {
                onCancel.accept(player, this.getConfirmAction());
            }
        };
    }

    @Override
    public AbstractVirtualAnvil createVirtualAnvil(Player player, String startText, BiConsumer<Player, ConfirmAction> onCancel, BiFunction<Player, String, ConfirmAction> confirm) {
        return new VirtualAnvil(player, startText) {
            @Override
            public void onConfirm(String text) {
                ConfirmAction action = confirm.apply(this.player, text);
                setConfirmAction(action);
                if (action == ConfirmAction.CONFIRMED)
                    FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
                        player.closeInventory();
                    });
            }

            @Override
            public void onCancel() {
                onCancel.accept(player, this.getConfirmAction());
            }
        };
    }

    @Override
    public AbstractVirtualAnvil createVirtualAnvil(Player player, String startText, ItemStack displayItem, BiConsumer<Player, ConfirmAction> onCancel, BiFunction<Player, String, ConfirmAction> confirm) {
        return new VirtualAnvil(player, startText, displayItem) {
            @Override
            public void onConfirm(String text) {
                ConfirmAction action = confirm.apply(this.player, text);
                setConfirmAction(action);
                if (action == ConfirmAction.CONFIRMED)
                    player.closeInventory();
            }

            @Override
            public void onCancel() {
                onCancel.accept(player, this.getConfirmAction());
            }
        };
    }
}
