package net.drapuria.framework.bukkit.inventory.menu.factory;

import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.IMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractMenuFactory<F extends AbstractMenuFactory<F, B>, B extends IButton> {

    protected Function<Player, String> title;
    protected Function<Player, Integer> size;
    protected Function<Player, InventoryType> inventoryType;

    protected final Map<Integer, Function<Player, B>> buttons = new HashMap<>();


    @SuppressWarnings("unchecked")
    public F title(Function<Player, String> title) {
        this.title = title;
        return (F) this;
    }

    @SuppressWarnings("unchecked")
    public F size(Function<Player, Integer> size) {
        this.size = size;
        return (F) this;
    }

    @SuppressWarnings("unchecked")
    public F inventoryType(Function<Player, InventoryType> inventoryType) {
        this.inventoryType = inventoryType;
        return (F) this;
    }

    public F button(Integer slot, Function<Player, B> button) {
        buttons.put(slot, button);
        return (F) this;
    }

    public abstract IMenu buildMenu();


}
