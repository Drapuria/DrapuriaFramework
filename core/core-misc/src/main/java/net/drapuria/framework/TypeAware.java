/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework;

import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;

/**
 * Represents an object that knows it´s own type parameter
 *
 * @param <T> The {@link Object objects} type
 */
public interface TypeAware<T> {

    /**
     * @return The Type of the {@link Object object}
     */
    @Nonnull
    TypeToken<T> getType();

}
