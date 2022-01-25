/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util.terminable.module;




import net.drapuria.framework.util.terminable.TerminableConsumer;

import javax.annotation.Nonnull;

/**
 * A terminable module is a class which manipulates and constructs a number
 * of {@link net.drapuria.framework.util.terminable.Terminable}s.
 *
 * @author lucko
 */
public interface TerminableModule {

    /**
     * Performs the tasks to setup this module
     *
     * @param consumer the terminable consumer
     */
    void setup(@Nonnull TerminableConsumer consumer);

    /**
     * Registers this terminable with a terminable consumer
     *
     * @param consumer the terminable consumer
     */
    default void bindModuleWith(@Nonnull TerminableConsumer consumer) {
        consumer.bindModule(this);
    }

}
