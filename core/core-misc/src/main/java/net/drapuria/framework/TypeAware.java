/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework;

import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;

public interface TypeAware<T> {

    /**
     * Represents an object that knows it´s own type parameter
     * @return <T> the type
     */
    @Nonnull
    TypeToken<T> getType();

}
