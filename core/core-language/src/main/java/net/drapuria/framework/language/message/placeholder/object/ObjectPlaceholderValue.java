package net.drapuria.framework.language.message.placeholder.object;

import net.drapuria.framework.language.message.placeholder.object.transformer.SimplePlaceholderTransformer;

import java.util.Locale;

public class ObjectPlaceholderValue<O> extends PlaceholderValue<O, SimplePlaceholderTransformer<O>>{
    protected ObjectPlaceholderValue(SimplePlaceholderTransformer<O> transformer) {
        super(transformer);
    }

    protected ObjectPlaceholderValue(Class<O> objectClass, String placeholder, SimplePlaceholderTransformer<O> transformer) {
        super(objectClass, placeholder, transformer);
    }

    public ObjectPlaceholderValue(String placeholder, SimplePlaceholderTransformer<O> transformer) {
        super(placeholder, transformer);
    }

    public ObjectPlaceholderValue(Class<O> objectClass, SimplePlaceholderTransformer<O> transformer) {
        super(objectClass, transformer);
    }

    @Override
    public String getValue(Object toTranslate, Locale ignored) {
        return super.transformer.transform((O) toTranslate);
    }
}