package net.drapuria.framework.language.message.placeholder.object.transformer;

import java.util.Locale;

public interface LocalePlaceholderTransformer<T> extends PlaceholderTransformer<T> {

    String transform(T object, Locale locale);

}
