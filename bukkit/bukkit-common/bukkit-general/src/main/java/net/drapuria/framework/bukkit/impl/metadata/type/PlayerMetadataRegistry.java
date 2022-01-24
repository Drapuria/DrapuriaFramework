package net.drapuria.framework.bukkit.impl.metadata.type;

import net.drapuria.framework.metadata.MetadataKey;
import net.drapuria.framework.metadata.MetadataMap;
import net.drapuria.framework.metadata.MetadataRegistry;
import org.bukkit.entity.Player;


import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Player}s.
 */
public interface PlayerMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given player.
     *
     * @param player the player
     * @return a metadata map
     */
    @Nonnull
    MetadataMap provide(@Nonnull Player player);

    /**
     * Gets a {@link MetadataMap} for the given player, if one already exists and has
     * been cached in this registry.
     *
     * @param player the player
     * @return a metadata map, if present
     */
    @Nonnull
    Optional<MetadataMap> get(@Nonnull Player player);

    /**
     * Gets a map of the players with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of players to key value
     */
    @Nonnull
    <K> Map<Player, K> getAllWithKey(@Nonnull MetadataKey<K> key);

}
