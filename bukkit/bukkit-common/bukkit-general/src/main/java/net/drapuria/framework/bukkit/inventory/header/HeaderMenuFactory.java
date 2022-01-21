/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.header;

import net.drapuria.framework.bukkit.inventory.deprecatedmenu.header.HeaderButton;
import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.header.controller.HeaderController;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderMenuFactory<H> {

    private static ItemStack RED_PANE;
    private static Button RED_PANE_BUTTON;
    private static ItemStack LIME_PANE;
    private static Button LIME_PANE_BUTTON;

    static {

    }

    private final List<BukkitHeaderIcon<H>> icons = new ArrayList<>();

    public Map<Integer, Button> buildMenuButtons(final Player shownTo, final HeaderController<H> controller) {
        Map<Integer, Button> buttons = new HashMap<>();
        final List<BukkitHeaderIcon<H>> icons = new ArrayList<>();

        for (BukkitHeaderIcon<H> icon : this.icons) {
            if (shownTo.hasPermission(icon.getViewPermission()))
                icons.add(icon);
        }
        final int middle = 4;
        if (icons.size() % 2 == 0) {
            int pair = 0;
            int slot = middle - (icons.size() / 2 * 3);
            int pairs = 0;
            if (slot < 1)
                slot = icons.size() / 3 == 2 ? 0 : 1;
            for (BukkitHeaderIcon<H> icon : icons) {
                buttons.put(slot, new HeaderButton<>(controller, icon));
                if (controller.getSelected() == icon.getAssignedTo())
                    buttons.put(slot + 9, LIME_PANE_BUTTON);
                else
                    buttons.put(slot + 9, RED_PANE_BUTTON);
                slot++;
                if (pairs == 1 && pair == 0 && icons.size() / 3 == 2)
                    slot++;
                pair++;
                if (icons.size() == 2) {
                    slot = middle + (icons.size() / 2 * 3);
                } else if (pair == 2) {
                    pair = 0;
                    slot++;
                    pairs++;
                    if (slot == middle) {
                        slot += 2;
                    }
                }
            }
        } else if (icons.size() == 1) {
            buttons.put(middle, new HeaderButton<>(controller, icons.get(0)));
            if (controller.getSelected() == icons.get(0).getAssignedTo())
                buttons.put(middle + 9, LIME_PANE_BUTTON);
            else
                buttons.put(middle + 9, RED_PANE_BUTTON);
        } else {
            int right;
            int left = right = (icons.size() - 1) / 2;
            right = right + middle + 1;
            left = middle - left;
            int index = 0;
            for (int slot = left; slot < right; slot++) {
                BukkitHeaderIcon<H> icon = icons.get(index);
                buttons.put(slot, new HeaderButton<>(controller, icon));
                if (controller.getSelected() == icon.getAssignedTo())
                    buttons.put(slot + 9, LIME_PANE_BUTTON);
                else
                    buttons.put(slot + 9, RED_PANE_BUTTON);
                index++;
            }
        }
        return buttons;
    }

    public void addIcon(BukkitHeaderIcon<H> icon) {
        icons.add(icon);
    }
}