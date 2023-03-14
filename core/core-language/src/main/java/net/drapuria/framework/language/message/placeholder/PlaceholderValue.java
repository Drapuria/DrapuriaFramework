package net.drapuria.framework.language.message.placeholder;

public class PlaceholderValue<O> implements IPlaceholderValue {

    private final String placeholder;
    private final PlaceholderTransformer<O> transformer;
    private final O objectToTransform;
    protected PlaceholderValue(final O object, final PlaceholderTransformer<O> transformer) {
        this.placeholder = null;
        this.objectToTransform = object;
        this.transformer = transformer;
    }

    protected PlaceholderValue(String placeholder, final O object, final PlaceholderTransformer<O> transformer) {
        this.placeholder = placeholder;
        this.objectToTransform = object;
        this.transformer = transformer;
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public String getValue() {
        return this.transformer.transform(objectToTransform);
    }

    public static <O> PlaceholderValue<O> of(final String placeholder, final O object, final PlaceholderTransformer<O> transformer) {
        return new PlaceholderValue<>(placeholder, object, transformer);
    }

    public static <O> PlaceholderValue<O> of(final O object, final PlaceholderTransformer<O> transformer) {
        return new PlaceholderValue<>(object, transformer);
    }
}