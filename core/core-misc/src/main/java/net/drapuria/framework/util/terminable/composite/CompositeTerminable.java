/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util.terminable.composite;


import net.drapuria.framework.util.terminable.Terminable;
import net.drapuria.framework.util.terminable.TerminableConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a {@link Terminable} made up of several other {@link Terminable}s.
 *
 * <p>The {@link #close()} method closes in LIFO (Last-In-First-Out) order.</p>
 *
 * <p>{@link Terminable}s can be reused. The instance is effectively
 * cleared on each invocation of {@link #close()}.</p>
 *
 * @author lucko
 */
public interface CompositeTerminable extends Terminable, TerminableConsumer {

    /**
     * Creates a new standalone {@link CompositeTerminable}.
     *
     * @return a new {@link CompositeTerminable}
     */
    @Nonnull
    static CompositeTerminable create() {
        return new AbstractCompositeTerminable();
    }

    /**
     * Creates a new standalone {@link CompositeTerminable}, which wraps
     * contained terminables in {@link java.lang.ref.WeakReference}s.
     *
     * @return a new {@link CompositeTerminable}
     */
    @Nonnull
    static CompositeTerminable createWeak() {
        return new AbstractWeakCompositeTerminable();
    }

    /**
     * Closes this composite terminable.
     *
     * @throws CompositeClosingException if any of the sub-terminables throw an
     *                                   exception on close
     */
    @Override
    void close() throws CompositeClosingException;

    @Nullable
    @Override
    default CompositeClosingException closeSilently() {
        try {
            close();
            return null;
        } catch (CompositeClosingException e) {
            return e;
        }
    }

    @Override
    default void closeAndReportException() {
        try {
            close();
        } catch (CompositeClosingException e) {
            e.printAllStackTraces();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Binds an {@link AutoCloseable} with this composite closable.
     *
     * <p>Note that implementations do not keep track of duplicate contained
     * terminables. If a single {@link AutoCloseable} is added twice, it will be
     * {@link AutoCloseable#close() closed} twice.</p>
     *
     * @param autoCloseable the closable to bind
     * @throws NullPointerException if the closable is null
     * @return this (for chaining)
     */
    CompositeTerminable with(AutoCloseable autoCloseable);

    /**
     * Binds all given {@link AutoCloseable} with this composite closable.
     *
     * <p>Note that implementations do not keep track of duplicate contained
     * terminables. If a single {@link AutoCloseable} is added twice, it will be
     * {@link AutoCloseable#close() closed} twice.</p>
     *
     * <p>Ignores null values.</p>
     *
     * @param autoCloseables the closables to bind
     * @return this (for chaining)
     */
    default CompositeTerminable withAll(AutoCloseable... autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable == null) {
                continue;
            }
            bind(autoCloseable);
        }
        return this;
    }

    /**
     * Binds all given {@link AutoCloseable} with this composite closable.
     *
     * <p>Note that implementations do not keep track of duplicate contained
     * terminables. If a single {@link AutoCloseable} is added twice, it will be
     * {@link AutoCloseable#close() closed} twice.</p>
     *
     * <p>Ignores null values.</p>
     *
     * @param autoCloseables the closables to bind
     * @return this (for chaining)
     */
    default CompositeTerminable withAll(Iterable<? extends AutoCloseable> autoCloseables) {
        for (AutoCloseable autoCloseable : autoCloseables) {
            if (autoCloseable == null) {
                continue;
            }
            bind(autoCloseable);
        }
        return this;
    }

    @Nonnull
    @Override
    default <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        with(terminable);
        return terminable;
    }

    /**
     * Removes instances which have already been terminated.
     */
    void cleanup();

}
