package net.drapuria.framework.bukkit.impl.metadata.type;

import net.drapuria.framework.metadata.MetadataKey;
import net.drapuria.framework.metadata.MetadataMap;
import net.drapuria.framework.metadata.MetadataRegistry;
import org.bukkit.World;


import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link World}s.
 */
public interface WorldMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given world.
     *
     * @param world the world
     * @return a metadata map
     */
    @Nonnull
    MetadataMap provide(@Nonnull World world);

    /**
     * Gets a {@link MetadataMap} for the given world, if one already exists and has
     * been cached in this registry.
     *
     * @param world the world
     * @return a metadata map, if present
     */
    @Nonnull
    Optional<MetadataMap> get(@Nonnull World world);

    /**
     * Gets a map of the worlds with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of worlds to key value
     */
    @Nonnull
    <K> Map<World, K> getAllWithKey(@Nonnull MetadataKey<K> key);

}
