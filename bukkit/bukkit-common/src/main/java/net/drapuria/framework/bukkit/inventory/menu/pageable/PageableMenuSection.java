package net.drapuria.framework.bukkit.inventory.menu.pageable;


import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.Menu;
import net.drapuria.framework.bukkit.inventory.menu.buttons.DisplayButton;
import net.drapuria.framework.bukkit.inventory.menu.buttons.NextPageButton;
import net.drapuria.framework.bukkit.inventory.menu.buttons.PreviousPageButton;
import net.drapuria.framework.pageable.section.PageableGuiSection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class PageableMenuSection<I> extends PageableGuiSection<I, Menu, PaginatedButton> {
    private final int middle, next, last;
    private Button defaultButton;

    protected PageableMenuSection(@NotNull Menu menu, final int middle, final int next, final int last, @Nullable Button defaultButton) {
        super(menu);
        this.middle = middle;
        this.next = next;
        this.last = last;
        this.defaultButton = defaultButton;
    }

    public void setDefaultButton(Button defaultButton) {
        this.defaultButton = defaultButton;
    }

    public Map<Integer, Button> buildPage() {
        Map<Integer, Button> buttons = new HashMap<>();
        int index;
        int left, right;
        left = right = 3;
        right = right + middle + 1;
        left = middle - left;
        index = getPageSize() * this.getPage();
        if (emptyIfNoItem)
            for (int slot = left; slot < right; slot++)
                buttons.put(slot, new DisplayButton(new ItemStack(Material.AIR), true));
        if (super.getItems().isEmpty()) {
            if (defaultButton != null)
                buttons.put(middle, defaultButton);
        } else if (getItems().size() == 1) {
            buttons.put(middle, super.getDisplayAdapter(get(0)));
        } else {
            for (int slot = left; slot < right; slot++) {
                if (getItems().size() <= index)
                    continue;
                I item = get(index);
                buttons.put(slot, super.getDisplayAdapter(item));
                index++;
            }
            if (getItems().size() > index) {
                buttons.put(next, new NextPageButton() {
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        playNeutral(player);
                        setPage(getPage() + 1);
                        getGui().openMenu(player);
                    }
                });
            }
            if (getPage() > 0) {
                buttons.put(last, new PreviousPageButton() {
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        playNeutral(player);
                        setPage(getPage() - 1);
                        getGui().openMenu(player);
                    }
                });
            }
        }
        return buttons;
    }
}