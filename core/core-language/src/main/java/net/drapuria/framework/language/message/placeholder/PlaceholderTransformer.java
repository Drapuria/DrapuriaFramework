package net.drapuria.framework.language.message.placeholder;

public interface PlaceholderTransformer<T> {

    String transform(T object);

}
