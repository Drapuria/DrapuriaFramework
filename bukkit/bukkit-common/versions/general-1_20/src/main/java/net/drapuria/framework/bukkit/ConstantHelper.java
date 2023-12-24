/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit;

import net.drapuria.framework.BootstrapInvoke;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.header.HeaderMenuFactory;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.item.ItemConstants;
import net.drapuria.framework.bukkit.scoreboard.board.DrapuriaBoard;
import net.drapuria.framework.bukkit.sound.SoundConstants;
import net.drapuria.framework.bukkit.sound.SoundData;
import net.drapuria.framework.bukkit.util.DrapuriaSoundDefaults;
import net.minecraft.network.NetworkManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ConstantHelper {
    @BootstrapInvoke
    public static void loadConstants() {
        DrapuriaBoard.MAX_PREFIX_SUFFIX_LENGTH = 40;
        ItemConstants.BARRIER = ItemBuilder.of(Material.BARRIER).build();
        ItemConstants.RED_PANE = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE).build();
        ItemConstants.PLACEHOLDER = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).build();

        SoundConstants.FAIL = SoundData.of(Sound.BLOCK_NOTE_BLOCK_BASS);
        SoundConstants.SUCCESS = SoundData.of(Sound.BLOCK_NOTE_BLOCK_PLING);
        SoundConstants.NEUTRAL = SoundData.of(Sound.UI_BUTTON_CLICK);
        DrapuriaSoundDefaults.STICK_SOUND = SoundData.of(Sound.BLOCK_NOTE_BLOCK_BANJO);

        HeaderMenuFactory.LIME_PANE = ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).setDisplayName("")
                .build();
        HeaderMenuFactory.RED_PANE = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE).setDisplayName("").build();
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