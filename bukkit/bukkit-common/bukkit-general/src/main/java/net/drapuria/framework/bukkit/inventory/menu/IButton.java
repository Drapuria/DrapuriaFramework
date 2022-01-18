package net.drapuria.framework.bukkit.inventory.menu;

import net.drapuria.framework.bukkit.sound.SoundData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface IButton {

    ItemStack getIcon();

    ItemStack setIcon(ItemStack inventory);

    void onClick(Player player, int slot, ClickType clickType, int hotbarButton);

    default boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    default boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return false;
    }

    default void playSuccess(Player player) {
        Sounds.successSound.play(player);
    }

    default void playNeutral(Player player) {
        Sounds.neutralSound.play(player);
    }

    default void playFail(Player player) {
        Sounds.failSound.play(player);
    }

    static class Sounds {
        private final static SoundData failSound = SoundData.of(Sound.NOTE_BASS);
        private final static SoundData successSound = SoundData.of(Sound.NOTE_PLING);
        private final static SoundData neutralSound = SoundData.of(Sound.CLICK);
    }

}
