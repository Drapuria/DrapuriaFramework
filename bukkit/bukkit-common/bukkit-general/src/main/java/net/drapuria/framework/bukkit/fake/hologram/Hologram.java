package net.drapuria.framework.bukkit.fake.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Hologram {

    Location getLocation();

    void show(final Player player);

    void hide(final Player player);


    void setLocation(Location location);

    void checkHologram();

}
