package net.drapuria.framework.bukkit;

import net.drapuria.framework.BootstrapInvoke;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.item.ItemConstants;
import net.drapuria.framework.bukkit.sound.SoundConstants;
import net.drapuria.framework.bukkit.sound.SoundData;
import net.drapuria.framework.bukkit.util.DrapuriaSoundDefaults;
import org.bukkit.Material;
import org.bukkit.Sound;

public class ConstantHelper {

    @BootstrapInvoke
    public static void loadConstants() {

        ItemConstants.BARRIER = ItemBuilder.of(Material.BARRIER).build();
        ItemConstants.RED_PANE = ItemBuilder.of(Material.STAINED_GLASS_PANE).setDurability(14).build();
        ItemConstants.PLACEHOLDER = ItemBuilder.of(Material.STAINED_GLASS_PANE).setDurability(7).build();

        SoundConstants.FAIL = SoundData.of(Sound.NOTE_BASS);
        SoundConstants.SUCCESS = SoundData.of(Sound.NOTE_PLING);
        SoundConstants.NEUTRAL = SoundData.of(Sound.CLICK);
        DrapuriaSoundDefaults.STICK_SOUND = SoundData.of(Sound.NOTE_STICKS);

    }

}
