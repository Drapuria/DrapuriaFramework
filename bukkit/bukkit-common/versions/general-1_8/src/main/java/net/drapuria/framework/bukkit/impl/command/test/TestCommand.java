/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.test;

import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.Menu;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.DefaultCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Command(names = "test", useSubCommandsOnly = true)
public class TestCommand extends DrapuriaCommand {

    @DefaultCommand
    public void onCommand(Player player) {
        new Menu() {
            @Override
            public String getTitle(Player player) {
                return "HI";
            }

            @Override
            public int getSize(Player player) {
                return 54;
            }

            @Override
            public InventoryType getBukkitInventoryType(Player player) {
                return null;
            }

            @Override
            public Map<Integer, IButton> getButtons(Player player) {
                Map<Integer, IButton> buttons = new HashMap<>();
                buttons.put(2, new Button() {
                    @Override
                    public ItemStack getIcon(Player player) {
                        return ItemBuilder.of(Material.BEDROCK).setDisplayName("a").build();
                    }

                    @Override
                    public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {
                        player.sendMessage("HI");
                    }
                });
                return buttons;
            }
        }.openMenu(player);
    }

}
