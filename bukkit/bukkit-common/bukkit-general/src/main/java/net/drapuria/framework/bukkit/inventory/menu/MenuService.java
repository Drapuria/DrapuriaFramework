/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import net.drapuria.framework.beans.annotation.Service;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, IMenu> getOpenedMenus() {
        return openedMenus;
    }
}
