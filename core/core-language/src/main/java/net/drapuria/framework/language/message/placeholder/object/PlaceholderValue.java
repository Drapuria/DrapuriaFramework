package net.drapuria.framework.language.message.placeholder.object;

import net.drapuria.framework.language.message.placeholder.IPlaceholderValue;
import net.drapuria.framework.language.message.placeholder.object.transformer.LocalePlaceholderTransformer;
import net.drapuria.framework.language.message.placeholder.object.transformer.PlaceholderTransformer;
import net.drapuria.framework.language.message.placeholder.object.transformer.SimplePlaceholderTransformer;

public abstract class PlaceholderValue<O, T> implements IPlaceholderValue {

    private final String placeholder;
    protected final T transformer;
    private final Class<O> objectClass;
    protected PlaceholderValue(final T transformer) {
        this.placeholder = null;
        this.transformer = transformer;
        this.objectClass = null;
    }

    protected PlaceholderValue(Class<O> objectClass, String placeholder,T transformer) {
        this.placeholder = placeholder;
        this.transformer = transformer;
        this.objectClass = objectClass;
    }

    public PlaceholderValue(String placeholder,T transformer) {
        this(null, placeholder,  transformer);
    }

    public PlaceholderValue(final Class<O> objectClass, T transformer) {
        this(objectClass, null,  transformer);
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }


    public static <O> PlaceholderValue of(final Class<O> objectClass, final PlaceholderTransformer<O> transformer) {
        if (transformer instanceof LocalePlaceholderTransformer) {
            final LocalePlaceholderTransformer<O> localePlaceholderValue = (LocalePlaceholderTransformer<O>) transformer;
            return new LocaleObjectPlaceholderValue<>(objectClass, localePlaceholderValue);
        }
        final SimplePlaceholderTransformer<O> simplePlaceholderTransformer = (SimplePlaceholderTransformer<O>) transformer;
        return new ObjectPlaceholderValue<>(objectClass, simplePlaceholderTransformer);
    }

    public static <O> PlaceholderValue of(final String placeholder, final PlaceholderTransformer<O> transformer) {
        if (transformer instanceof LocalePlaceholderTransformer) {
            final LocalePlaceholderTransformer<O> localePlaceholderValue = (LocalePlaceholderTransformer<O>) transformer;
            return new LocaleObjectPlaceholderValue<>(placeholder, localePlaceholderValue);
        }
        final SimplePlaceholderTransformer<O> simplePlaceholderTransformer = (SimplePlaceholderTransformer<O>) transformer;
        return new ObjectPlaceholderValue<>(placeholder, simplePlaceholderTransformer);
    }

    public static <O> PlaceholderValue of(final PlaceholderTransformer<O> transformer) {
        if (transformer instanceof LocalePlaceholderTransformer) {
            final LocalePlaceholderTransformer<O> localePlaceholderValue = (LocalePlaceholderTransformer<O>) transformer;
            return new LocaleObjectPlaceholderValue<>(localePlaceholderValue);
        }
        final SimplePlaceholderTransformer<O> simplePlaceholderTransformer = (SimplePlaceholderTransformer<O>) transformer;
        return new ObjectPlaceholderValue<>(simplePlaceholderTransformer);
    }
}