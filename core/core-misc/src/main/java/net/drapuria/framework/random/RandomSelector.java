/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.random;

import net.drapuria.framework.random.weight.Weigher;
import net.drapuria.framework.random.weight.Weighted;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @param <E> the element type
 */
public interface RandomSelector<E> {

    /**
     * Creates a uniform selector which picks elements randomly.
     *
     * @param elements the elements to pick from
     * @param <E> the element type
     * @return the selector instance
     */
    static <E> RandomSelector<E> uniform(Collection<E> elements) {
        return DefaultRandomSelector.uniform(elements);
    }

    /**
     * Creates a weighted selector which picks elements according to the value of their {@link Weighted#getWeight()}.
     *
     * @param elements the elements to pick from
     * @param <E> the element type
     * @return the selector instance
     */
    static <E extends Weighted> RandomSelector<E> weighted(Collection<E> elements) {
        return weighted(elements, Weighted.WEIGHER);
    }

    /**
     * Creates a weighted selector which picks elements using their weight,
     * according to the weigher function.
     *
     * @param elements the elements to pick from
     * @param <E> the element type
     * @return the selector instance
     */
    static <E> RandomSelector<E> weighted(Collection<E> elements, Weigher<? super E> weigher) {
        return DefaultRandomSelector.weighted(elements, weigher);
    }


    E pick(Random random);

    Stream<E> stream(Random random);


}
