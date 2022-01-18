package net.drapuria.framework.bukkit.inventory.deprecatedmenu.pageable;

import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Menu;
import org.bukkit.entity.Player;

import java.util.Map;

public class PageableMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return null;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return null;
    }
}
