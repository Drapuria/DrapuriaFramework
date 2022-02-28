/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.bukkit.impl.annotation.ServerImpl;
import net.drapuria.framework.bukkit.inventory.anvil.AbstractVirtualAnvil;
import net.drapuria.framework.bukkit.inventory.anvil.ConfirmAction;
import net.drapuria.framework.bukkit.inventory.anvil.VirtualAnvil;
import net.drapuria.framework.bukkit.util.BlockPosition;
import net.drapuria.framework.bukkit.util.Skin;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
public class ServerImplementation1_8 implements ServerImplementation {
    @Override
    public Entity getEntity(UUID uuid) {
        return null;
    }

    @Override
    public Entity getEntity(World world, int id) {
        return null;
    }

    @Override
    public Object toBlockNMS(MaterialData materialData) {
        return null;
    }

    @Override
    public List<Player> getPlayersInRadius(Location location, double radius) {
        return location.getWorld().getNearbyEntities(location, radius / 2, radius / 2, radius / 2)
                .stream()
                .filter(entity -> entity instanceof Player)
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
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        GameProfile profile = entityPlayer.getProfile();
        if (!profile.getProperties().get("textures").isEmpty()) {
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new Skin(texture, signature);
        }
        return null;
    }

    @Override
    public AbstractVirtualAnvil createVirtualAnvil(Player player, BiConsumer<Player, ConfirmAction> onCancel, BiFunction<Player, String, ConfirmAction> confirm) {
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
                    player.closeInventory();
            }

            @Override
            public void onCancel() {
                if (this.getConfirmAction() == ConfirmAction.CONFIRMED)
                    DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> onCancel.accept(player, this.getConfirmAction()), 1L);
                else
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
                if (this.getConfirmAction() == ConfirmAction.CONFIRMED)
                    DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> onCancel.accept(player, this.getConfirmAction()), 1L);
                else
                    onCancel.accept(player, this.getConfirmAction());            }
        };
    }
}
