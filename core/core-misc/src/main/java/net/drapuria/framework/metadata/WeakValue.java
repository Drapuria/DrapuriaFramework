package net.drapuria.framework.metadata;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a value wrapped in a {@link WeakReference}
 *
 * @param <T> the wrapped value type
 */
public final class WeakValue<T> implements TransientValue<T> {

    public static <T> WeakValue<T> of(T value) {
        Objects.requireNonNull(value, "value");
        return new WeakValue<>(value);
    }

    public static <T> Supplier<WeakValue<T>> supplied(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");

        return () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value");

            return new WeakValue<>(value);
        };
    }

    private final WeakReference<T> value;

    private WeakValue(T value) {
        this.value = new WeakReference<>(value);
    }

    @Nullable
    @Override
    public T getOrNull() {
        return this.value.get();
    }

    @Override
    public boolean shouldExpire() {
        return this.value.get() == null;
    }

}
