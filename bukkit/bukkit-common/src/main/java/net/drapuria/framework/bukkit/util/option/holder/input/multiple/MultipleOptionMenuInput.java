package net.drapuria.framework.bukkit.util.option.holder.input.multiple;


import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;
import net.drapuria.framework.bukkit.util.option.holder.AbstractSwitchableHolderOption;
import net.drapuria.framework.bukkit.util.option.holder.SwitchableHolderOption;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.Menu;
import net.drapuria.framework.bukkit.inventory.menu.buttons.BackButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class MultipleOptionMenuInput {

    private static Map<Player, OptionMenu<?>> options = new HashMap<>();

    public abstract void create();

    public static class OptionMenu<H> {
        private final H holder;
        private final String menuTitle;
        private final Player player;
        private final Map<Integer, AbstractHolderOption<?, ?>> options;

        private final BackButton backButton;
        private final Menu menu;

        public OptionMenu(Player player, H holder, String menuTitle, Map<Integer, AbstractHolderOption<?, ?>> options, BackButton backButton) {
            this.holder = holder;
            this.menuTitle = menuTitle;
            this.player = player;
            this.options = options;
            this.backButton = backButton;
            this.menu = new Menu() {
                @Override
                public String getTitle(Player player) {
                    return menuTitle;
                }

                @Override
                public Map<Integer, Button> getButtons(Player player) {
                    Map<Integer, Button> buttons = new HashMap<>();

                    for (Map.Entry<Integer, AbstractHolderOption<?, ?>> entry : options.entrySet()) {
                        AbstractHolderOption<?, ?> option = entry.getValue();
                        buttons.put(entry.getKey(), new Button() {
                            @Override
                            public ItemStack getButtonItem(Player player) {
                                return option.getDisplayAdapter().generateDisplayItem(player);
                            }

                            @Override
                            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                                if (option instanceof SwitchableHolderOption) {
                                    AbstractSwitchableHolderOption<?, ?> switchableOption = (AbstractSwitchableHolderOption<?, ?>) option;
                                    switchableOption.switchValues(player);
                                    updateSlot(player, slot);
                                    player.playSound(player.getLocation(), Sound.CLICK, 1L, 1L);
                                } else {
                                    player.playSound(player.getLocation(), Sound.CLICK, 1L, 1L);
                                    option.getOptionInput().startInput(player,  option);
                                }
                            }
                        });
                    }
                    if (backButton != null)
                        buttons.put(0, backButton);

                    return buttons;
                }

                @Override
                public void onClose(Player player) {
                    if (!isClosedByMenu()) {
                        if (backButton != null && backButton.getBack() != null)
                            backButton.getBack().openMenu(player);
                    }
                }
            };
            this.menu.openMenu(player);
        }

        public Player getPlayer() {
            return player;
        }

        public Menu getMenu() {
            return menu;
        }


        public Map<Integer, AbstractHolderOption<?, ?>> getOptions() {
            return options;
        }
    }
}