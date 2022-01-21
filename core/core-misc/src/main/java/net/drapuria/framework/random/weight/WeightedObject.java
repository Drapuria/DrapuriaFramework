/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.random.weight;

import javax.annotation.Nonnull;
import java.util.Objects;

public class WeightedObject<T> implements Weighted {

    @Nonnull
    public static <T> WeightedObject<T> of(@Nonnull T object, double weight) {
        return new WeightedObject<>(object, weight);
    }

    private final T object;
    private double weight;

    public WeightedObject(T object, double weight) {
        this.object = object;
        this.weight = weight;
    }

    @Nonnull
    public T get() {
        return object;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WeightedObject<?> that = (WeightedObject<?>) o;
        return Double.compare(that.weight, weight) == 0 && Objects.equals(object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, weight);
    }

    @Override
    public String toString() {
        return "WeightedObject{" +
                "object=" + object +
                ", weight=" + weight +
                '}';
    }
}
