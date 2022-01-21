/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util.terminable;

import net.drapuria.framework.util.terminable.module.TerminableModule;

import javax.annotation.Nonnull;

/**
 * Accepts {@link AutoCloseable}s (and by inheritance {@link Terminable}s),
 * as well as {@link TerminableModule}s.
 *
 * @author lucko
 */
@FunctionalInterface
public interface TerminableConsumer {

    /**
     * Binds with the given terminable.
     *
     * @param terminable the terminable to bind with
     * @param <T> the terminable type
     * @return the same terminable
     */
    @Nonnull
    <T extends AutoCloseable> T bind(@Nonnull T terminable);

    /**
     * Binds with the given terminable module.
     *
     * @param module the module to bind with
     * @param <T> the module type
     * @return the same module
     */
    @Nonnull
    default <T extends TerminableModule> T bindModule(@Nonnull T module) {
        module.setup(this);
        return module;
    }

}
