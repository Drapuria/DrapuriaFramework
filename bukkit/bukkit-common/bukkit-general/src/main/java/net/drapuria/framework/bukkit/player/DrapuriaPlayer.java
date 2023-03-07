/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.language.aware.LocalizedMessageSender;
import net.drapuria.framework.scheduler.factory.SchedulerFactory;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

/**
 * Represents a Player
 */
public interface DrapuriaPlayer extends Player, Permissible, LocalizedMessageSender {

    void sendActionBar(String text);

    default void sendActionBar(String text, long seconds) {
        new SchedulerFactory<Runnable>()
                .supplier(() -> () -> sendActionBar(text))
                .repeated((aLong, runnable) -> runnable.run())
                .iterations(seconds * 10)
                .period(2)
                .delay(0)
                .build();
    }

    void sendTitle(String text, int fadein, int showtime, int fadeout);

    void sendSubTitle(String text, int fadein, int showtime, int fadeout);

    boolean hasFullInventory();

    boolean hasEmptyInventory();

    void giveItem(final ItemStack item);


    long getSessionJoin();

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