package net.drapuria.framework.bukkit.fake.hologram;

import net.drapuria.framework.bukkit.fake.FakeShowType;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * A Hologram is a packet based {@link org.bukkit.entity.ArmorStand} displaying text or items
 */
public interface Hologram {

    /**
     * @return The location of the hologram
     */
    Location getLocation();

    /**
     * Shows the hologram to a specific player
     *
     * @param player the player the hologram gets shown to
     */
    void show(final Player player);

    /**
     * Hides the hologram from a specific player
     *
     * @param player the player the hologram gets hidden from
     */
    void hide(final Player player);

    /**
     * Sets the hologram location.
     * @param location the new location
     */
    void setLocation(Location location);

    /**
     * Checks the hologram
     */
    void checkHologram();

    /**
     * This only works if updates are not handled via Events.
     *
     * @param boundToPlayer if true the hologram will be bound to a player.
     */
    void setLocationBoundToPlayer(final boolean boundToPlayer);

    /**
     * This only works if updates are not handled via Events.
     *
     * @param boundToPlayer if true the hologram will be bound to a player.
     * @param player The player the hologram should be bounded to.
     */
    void setLocationBoundToPlayer(final boolean boundToPlayer, final Player player);

    /**
    * This only works if updates are not handled via Events.
     *
     * @return true if the hologram is bound to a player.
     */
    boolean isLocationBoundToPlayer();

    /**
     * This only works if updates are not handled via Events.
     *
     * @return the Player the hologram is bound to.
     */
    Player getBoundPlayer();

    /**
     * This only works if updates are not handled via Events.
     *
     * @param player The player the hologram is bound to.
     */
    void setBoundPlayer(final Player player);

    /**
     * This only works if updates are not handled via Events.
     *
     * @param yOffset the y offset
     */
    void setBoundYOffset(double yOffset);

    /**
     * @return The yOffset
     */
    double getBoundYOffset();

    /**
     * The {@link FakeShowType} defines if the hologram should be shown to all players.
     * {@link FakeShowType#ALL} to have no special settings.
     * {@link FakeShowType#EXCLUDING} to exclude players from seeing the hologram.
     * {@link FakeShowType#INCLUDING} to include players from seeing the hologram.
     *
     * @return The current {@link FakeShowType}.
     */
    FakeShowType getType();

    /**
     * Sets the {@link FakeShowType} of the hologram.
     * {@link FakeShowType#ALL} to have no special settings.
     * {@link FakeShowType#EXCLUDING} to exclude players from seeing the hologram.
     * {@link FakeShowType#INCLUDING} to include players from seeing the hologram.
     *
     * @param fakeShowType the new {@link FakeShowType}.
     */
    void setType(final FakeShowType fakeShowType);

    /**
     * Update the content of a {@link Line}
     *
     * @param line the {@link Line} to update
     */

    void updateLine(Line line);

    /**
     * Update the content of all {@link net.drapuria.framework.bukkit.fake.hologram.line.ConsumedTextLine}
     *
     */

    void updateConsumedLines();

    /**
     * A {@link List} of excluded or included {@link Player players}
     *
     * @return All the excluded or included {@link Player players}.
     */
    List<Player> getIncludedOrExcludedPlayers();

    /**
     * Exclude or include a {@link Player}.
     *
     * @param player the player to include or exclude
     */
    void addExcludedOrIncludedPlayer(final Player player);

    /**
     * Remove a {@link Player} from being excluded or included.
     *
     * @param player the player to remove
     */
    void removeExcludedOrIncludedPlayer(final Player player);

    /**
     * Checks if a {@link Player} is excluded or included.
     *
     * @param player the player to check
     * @return true if in {@link Hologram#getIncludedOrExcludedPlayers()}.
     */
    boolean isExcludedOrIncluded(final Player player);

    default void updateBound() {
        if (this.getBoundPlayer() == null || !this.getBoundPlayer().isOnline()) {
            this.setLocationBoundToPlayer(false, null);
            return;
        }
        if (!BukkitUtil.locationEquals(this.getLocation().clone().subtract(0, this.getBoundYOffset(), 0), this.getBoundPlayer().getEyeLocation())) {
            this.setLocation(this.getBoundPlayer().getEyeLocation().add(0, this.getBoundYOffset(), 0));
        }
    }
}