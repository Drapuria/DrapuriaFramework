package net.drapuria.framework.bukkit;

import net.drapuria.framework.BootstrapInvoke;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.item.ItemConstants;
import net.drapuria.framework.bukkit.sound.SoundConstants;
import net.drapuria.framework.bukkit.sound.SoundData;
import org.bukkit.Material;
import org.bukkit.Sound;

public class ConstantHelper {

    @BootstrapInvoke
    public static void loadConstants() {
        ItemConstants.BARRIER = ItemBuilder.of(Material.BARRIER).build();
        ItemConstants.RED_PANE = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE).build();
        ItemConstants.PLACEHOLDER = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).build();

        SoundConstants.FAIL = SoundData.of(Sound.BLOCK_NOTE_BLOCK_BASS);
        SoundConstants.SUCCESS = SoundData.of(Sound.BLOCK_NOTE_BLOCK_PLING);
        SoundConstants.NEUTRAL = SoundData.of(Sound.UI_BUTTON_CLICK);
    }
}