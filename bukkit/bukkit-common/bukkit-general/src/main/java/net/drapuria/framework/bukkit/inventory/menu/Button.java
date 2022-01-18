package net.drapuria.framework.bukkit.inventory.menu;

import net.drapuria.framework.bukkit.sound.SoundData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Button {

    private final static SoundData failSound = SoundData.of(Sound.NOTE_BASS);
    private final static SoundData successSound = SoundData.of(Sound.NOTE_PLING);
    private final static SoundData neutralSound = SoundData.of(Sound.CLICK);

    public static Button placeholder(final Material material, final byte data, String... title) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                final ItemStack itemStack = new ItemStack(material, 1, data);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(StringUtils.join(title));
                itemStack.setItemMeta(meta);
                return itemStack;
            }
        };
    }

    public abstract ItemStack getButtonItem(Player player);

    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {}

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return false;
    }

    public static void playFail(Player player) {
        failSound.play(player);

    }

    public static void playSuccess(Player player) {
        successSound.play(player);
    }

    public static void playNeutral(Player player) {
        neutralSound.play(player);
    }

}
