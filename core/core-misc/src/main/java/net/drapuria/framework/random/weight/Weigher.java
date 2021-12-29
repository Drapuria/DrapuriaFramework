package net.drapuria.framework.random.weight;

import javax.annotation.Nonnegative;

public interface Weigher<E> {

    /**
     * @param element The element to calculate the weight for
     * @return the calculated weight
     */
    @Nonnegative
    double weigh(E element);

}
