package net.drapuria.framework.bukkit.impl.metadata.type;

import net.drapuria.framework.bukkit.util.BlockPosition;
import net.drapuria.framework.metadata.MetadataKey;
import net.drapuria.framework.metadata.MetadataMap;
import net.drapuria.framework.metadata.MetadataRegistry;
import org.bukkit.block.Block;


import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Block}s.
 */
public interface BlockMetadataRegistry extends MetadataRegistry<BlockPosition> {

    /**
     * Produces a {@link MetadataMap} for the given block.
     *
     * @param block the block
     * @return a metadata map
     */
    @Nonnull
    MetadataMap provide(@Nonnull Block block);

    /**
     * Gets a {@link MetadataMap} for the given block, if one already exists and has
     * been cached in this registry.
     *
     * @param block the block
     * @return a metadata map, if present
     */
    @Nonnull
    Optional<MetadataMap> get(@Nonnull Block block);

    /**
     * Gets a map of the blocks with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of blocks to key value
     */
    @Nonnull
    <K> Map<BlockPosition, K> getAllWithKey(@Nonnull MetadataKey<K> key);

}
