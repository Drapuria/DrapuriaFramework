package net.drapuria.framework.metadata;

import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a value wrapped in a {@link SoftReference}
 *
 * @param <T> the wrapped value type
 */
public final class SoftValue<T> implements TransientValue<T> {

    public static <T> SoftValue<T> of(T value) {
        Objects.requireNonNull(value, "value");
        return new SoftValue<>(value);
    }

    public static <T> Supplier<SoftValue<T>> supplied(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");

        return () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value");

            return new SoftValue<>(value);
        };
    }

    private final SoftReference<T> value;

    private SoftValue(T value) {
        this.value = new SoftReference<>(value);
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
