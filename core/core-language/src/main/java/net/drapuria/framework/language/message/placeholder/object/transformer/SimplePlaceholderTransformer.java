package net.drapuria.framework.language.message.placeholder.object.transformer;

public interface SimplePlaceholderTransformer<T> extends PlaceholderTransformer<T> {

    String transform(T object);

}
