package net.drapuria.framework.bukkit.tablist;

import net.drapuria.framework.bukkit.tablist.util.BufferedTabObject;
import org.bukkit.entity.Player;

import java.util.Set;

public interface DrapuriaTabAdapter {

    void tick();

    Set<BufferedTabObject> getSlots(Player player);

    String getFooter(Player player);

    String getHeader(Player player);

}