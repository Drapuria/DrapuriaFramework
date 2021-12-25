package net.drapuria.framework.util.cycle;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An infinite cycle of elements.
 *
 * @param <E> the element type
 * @author lucko
 */
public interface Cycle<E> {

    /**
     * Creates a new cycle of elements.
     *
     * <p>Changes to the supplying list are not reflected in the cycle.</p>
     *
     * @param objects the objects to form the cycle from
     * @param <E> the element type
     * @return the cycle
     */
    @Nonnull
    static <E> Cycle<E> of(@Nonnull List<E> objects) {
        //noinspection deprecation
        return new CycleImpl<>(objects);
    }

    /**
     * Gets the current position of the cursor, as as index relating to a
     * position in the backing list.
     *
     * @return the cursor position
     */
    int cursor();

    /**
     * Sets the cursor to a given index
     *
     * @param index the index to set the cursor to
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    void setCursor(int index);

    /**
     * Gets the current element
     *
     * @return the current element
     */
    @Nonnull
    E current();

    /**
     * Advances the cursor, and returns the next element.
     *
     * @return the next element
     */
    @Nonnull
    E next();

    /**
     * Retreats the counter, and returns the previous element.
     *
     * @return the previous element
     */
    @Nonnull
    E previous();

    /**
     * Returns the index of the next position in the cycle.
     *
     * @return the next position
     */
    int nextPosition();

    /**
     * Returns the index of the previous position in the cycle.
     *
     * @return the previous position
     */
    int previousPosition();

    /**
     * Returns the next element without advancing the cursor.
     *
     * @return the next element
     */
    @Nonnull
    E peekNext();

    /**
     * Returns the previous element without retreating the cursor.
     *
     * @return the previous element
     */
    @Nonnull
    E peekPrevious();

    /**
     * Gets the list currently backing this cycle
     *
     * <p>The returned list is immutable.</p>
     *
     * @return the backing list
     */
    @Nonnull
    List<E> getBacking();

    /**
     * Creates a copy of this cycle.
     *
     * <p>The returned cycle will contain the same elements as this cycle, but
     * its cursor will be reset to zero.</p>
     *
     * @return a copy of this cycle
     */
    Cycle<E> copy();

}
