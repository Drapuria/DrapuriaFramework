package net.drapuria.framework.metadata;

import com.github.benmanes.caffeine.cache.LoadingCache;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A registry which provides and stores {@link MetadataMap}s for a given type.
 *
 * @param <T> the type
 */
public interface MetadataRegistry<T> {

    /**
     * Produces a {@link MetadataMap} for the given object.
     *
     * @param id the object
     * @return a metadata map
     */
    @Nonnull
    net.drapuria.framework.metadata.MetadataMap provide(@Nonnull T id);

    /**
     * Gets a {@link MetadataMap} for the given object, if one already exists and has
     * been cached in this registry.
     *
     * @param id the object
     * @return a metadata map, if present
     */
    @Nonnull
    Optional<net.drapuria.framework.metadata.MetadataMap> get(@Nonnull T id);

    /**
     * Deletes the {@link MetadataMap} and all contained {@link MetadataKey}s for
     * the given object.
     *
     * @param id the object
     */
    void remove(@Nonnull T id);

    /**
     * Performs cache maintenance to remove empty map instances and expired transient values.
     */
    void cleanup();

    public LoadingCache<T, net.drapuria.framework.metadata.MetadataMap> cache();

}
