package net.drapuria.framework.metadata;

import com.google.common.reflect.TypeToken;
import net.drapuria.framework.TypeAware;

import java.util.Objects;
import java.util.UUID;

/**
 * A MetadataKey can be mapped to values in a {@link MetadataMap}.
 *
 * <p>Unlike a normal map key, a MetadataKey also holds the type of the values mapped to it.</p>
 *
 * @param <T> the value type
 */
public interface MetadataKey<T> extends TypeAware<T> {

    /**
     * Creates a MetadataKey with the given id and type
     *
     * @param id the id of the key
     * @param type the type of the value mapped to this key
     * @param <T> the value type
     * @return a new metadata key
     */
    static <T> MetadataKey<T> create(String id, TypeToken<T> type) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        return new MetadataKeyImpl<>(id, type);
    }

    /**
     * Creates a MetadataKey with the given id and type
     *
     * @param id the id of the key
     * @param clazz the class type of the value mapped to this key
     * @param <T> the value type
     * @return a new metadata key
     */
    static <T> MetadataKey<T> create(String id, Class<T> clazz) {
        return create(id, TypeToken.of(clazz));
    }

    static MetadataKey<Empty> createEmptyKey(String id) {
        return create(id, Empty.class);
    }

    static MetadataKey<String> createStringKey(String id) {
        return create(id, String.class);
    }

    static MetadataKey<Boolean> createBooleanKey(String id) {
        return create(id, Boolean.class);
    }

    static MetadataKey<Integer> createIntegerKey(String id) {
        return create(id, Integer.class);
    }

    static MetadataKey<Long> createLongKey(String id) {
        return create(id, Long.class);
    }

    static MetadataKey<Double> createDoubleKey(String id) {
        return create(id, Double.class);
    }

    static MetadataKey<Float> createFloatKey(String id) {
        return create(id, Float.class);
    }

    static MetadataKey<Short> createShortKey(String id) {
        return create(id, Short.class);
    }

    static MetadataKey<Character> createCharacterKey(String id) {
        return create(id, Character.class);
    }

    static MetadataKey<UUID> createUuidKey(String id) {
        return create(id, UUID.class);
    }

    /**
     * Gets the id of this key. May be automatically lowercase'd
     *
     * @return the id of this key
     */
    String getId();

    /**
     * Get the type of the value mapped to this key
     *
     * @return the type of the value
     */
    @Override
    TypeToken<T> getType();

    /**
     * Should remove on non exists
     * THIS ONLY WORKS WHEN A VALID ENTITY TYPE NOT EXISTS
     *
     * @return the option
     */
    boolean removeOnNonExists();

    /**
     * set Remove on non exists
     * THIS ONLY WORKS WHEN A VALID ENTITY TYPE NOT EXISTS
     *
     */
    void setRemoveOnNonExists(boolean bol);

    /**
     * Attempts to cast the given object to the return type of the key
     *
     * @param object the object to be casted
     * @return a casted object
     * @throws ClassCastException if the object cannot be casted
     */
    T cast(Object object) throws ClassCastException;

}