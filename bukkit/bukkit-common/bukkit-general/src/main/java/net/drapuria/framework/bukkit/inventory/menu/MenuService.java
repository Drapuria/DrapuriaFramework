/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import lombok.Getter;
import net.drapuria.framework.beans.annotation.Service;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Getter
@Service(name = "menus")
public class MenuService {

    public static MenuService getService;

    private final Map<String, IMenu> openedMenus = new HashMap<>();

    public MenuService() {
        getService = this;
    }

    public void addOpenedMenu(final String player, final IMenu menu) {
        this.openedMenus.put(player, menu);
    }

    public void removePlayer(final String player) {
        this.openedMenus.remove(player);
    }

    public IMenu getOpenedMenu(String player) {
        return this.openedMenus.get(player);
    }

}
