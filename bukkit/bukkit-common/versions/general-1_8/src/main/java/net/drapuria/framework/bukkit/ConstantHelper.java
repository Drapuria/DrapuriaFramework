/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit;

import net.drapuria.framework.BootstrapInvoke;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.header.HeaderMenuFactory;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.item.ItemConstants;
import net.drapuria.framework.bukkit.sound.SoundConstants;
import net.drapuria.framework.bukkit.sound.SoundData;
import net.drapuria.framework.bukkit.util.DrapuriaSoundDefaults;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.STAINED_GLASS_PANE;

public class ConstantHelper {

    @BootstrapInvoke
    public static void loadConstants() {

        ItemConstants.BARRIER = ItemBuilder.of(Material.BARRIER).build();
        ItemConstants.RED_PANE = ItemBuilder.of(STAINED_GLASS_PANE).setDurability(14).build();
        ItemConstants.PLACEHOLDER = ItemBuilder.of(STAINED_GLASS_PANE).setDurability(7).build();

        SoundConstants.FAIL = SoundData.of(Sound.NOTE_BASS);
        SoundConstants.SUCCESS = SoundData.of(Sound.NOTE_PLING);
        SoundConstants.NEUTRAL = SoundData.of(Sound.CLICK);
        DrapuriaSoundDefaults.STICK_SOUND = SoundData.of(Sound.NOTE_STICKS);

        HeaderMenuFactory.LIME_PANE = ItemBuilder.of(STAINED_GLASS_PANE).setDurability(5).setDisplayName("")
                .build();
        HeaderMenuFactory.RED_PANE = ItemBuilder.of(STAINED_GLASS_PANE).setDurability(14).setDisplayName("").build();
        HeaderMenuFactory.LIME_PANE_BUTTON = new Button() {
            @Override
            public ItemStack getIcon(Player player) {
                return HeaderMenuFactory.LIME_PANE;
            }
        };

        HeaderMenuFactory.RED_PANE_BUTTON = new Button() {
            @Override
            public ItemStack getIcon(Player player) {
                return HeaderMenuFactory.RED_PANE;
            }
        };

    }

}
