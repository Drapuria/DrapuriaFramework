/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.random.weight;

import javax.annotation.Nonnegative;

/**
 * Represents object with weight
 */
public interface Weighted {

    /**
     * Instance of {@link Weigher} which uses the {@link #getWeight()} method to determine the weight.
     */
    Weigher<? super  Weighted> WEIGHER = Weighted::getWeight;

    @Nonnegative
    double getWeight();

}
