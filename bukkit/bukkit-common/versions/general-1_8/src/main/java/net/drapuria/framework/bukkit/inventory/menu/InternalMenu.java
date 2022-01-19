package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InternalMenu extends Menu {

    private final Map<Integer, Function<Player, Button>> buttons;
    private final Function<Player, String> title;
    private final Function<Player, Integer> sizeFunction;
    private final Function<Player, InventoryType> inventoryType;

    public InternalMenu(Map<Integer, Function<Player, Button>> buttons, Function<Player, String> title, Function<Player, Integer> size, Function<Player, InventoryType> inventoryType) {
        this.buttons = buttons;
        this.title = title;
        this.sizeFunction = size;
        this.inventoryType = inventoryType;
    }


    @Override
    public String getTitle(Player player) {
        return title.apply(player);
    }

    @Override
    public int getSize(Player player) {
        if (sizeFunction != null)
            return sizeFunction.apply(player);
        return getDefaultSize();
    }

    @Override
    public InventoryType getBukkitInventoryType(Player player) {
        if (this.inventoryType != null)
            return this.inventoryType.apply(player);
        return null;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        return buttons.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().apply(player)));
    }
}
