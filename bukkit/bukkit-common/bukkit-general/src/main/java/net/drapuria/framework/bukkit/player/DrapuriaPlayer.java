/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a Player
 *
 */
public interface DrapuriaPlayer extends Player {

    void sendActionBar(String text);

    void sendTitle(String text, int fadein, int showtime, int fadeout);

    void sendSubTitle(String text, int fadein, int showtime, int fadeout);

    boolean hasFullInventory();

    boolean hasEmptyInventory();

    void giveItem(final ItemStack item);

    default void clearPotionEffects() {
        getActivePotionEffects().forEach(potionEffect -> removePotionEffect(potionEffect.getType()));
        setWalkSpeed(.2F);
        setFireTicks(0);
    }

    default void clear() {
        clearPotionEffects();
        clearPotionEffects();
        setGameMode(GameMode.SURVIVAL);
        setAllowFlight(false);
        setFlying(false);
        setMaximumNoDamageTicks(20);
        setFoodLevel(20);
        setHealth(getMaxHealth());
        setFallDistance(0F);
        getInventory().clear();
        getInventory().setArmorContents(null);
        getInventory().setHeldItemSlot(0);
        updateInventory();
    }


}
