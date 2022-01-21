/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import net.drapuria.framework.bukkit.sound.SoundConstants;
import net.drapuria.framework.bukkit.sound.SoundData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface IButton {

    ItemStack getIcon(Player player);

    void onClick(Player player, int slot, ClickType clickType, int hotbarButton);

    default boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    default boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return false;
    }

    default void playSuccess(Player player) {
        SoundConstants.SUCCESS.play(player);
    }

    default void playNeutral(Player player) {
        SoundConstants.NEUTRAL.play(player);
    }

    default void playFail(Player player) {
        SoundConstants.FAIL.play(player);
    }

}
