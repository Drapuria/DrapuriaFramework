package net.drapuria.framework.bukkit.impl.metadata.type;

import net.drapuria.framework.metadata.MetadataKey;
import net.drapuria.framework.metadata.MetadataMap;
import net.drapuria.framework.metadata.MetadataRegistry;
import org.bukkit.entity.Entity;


import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Entity}s.
 */
public interface EntityMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given entity.
     *
     * @param entity the entity
     * @return a metadata map
     */
    @Nonnull
    MetadataMap provide(@Nonnull Entity entity);

    /**
     * Gets a {@link MetadataMap} for the given entity, if one already exists and has
     * been cached in this registry.
     *
     * @param entity the entity
     * @return a metadata map, if present
     */
    @Nonnull
    Optional<MetadataMap> get(@Nonnull Entity entity);

    /**
     * Gets a map of the entities with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of entities to key value
     */
    @Nonnull
    <K> Map<Entity, K> getAllWithKey(@Nonnull MetadataKey<K> key);

}
