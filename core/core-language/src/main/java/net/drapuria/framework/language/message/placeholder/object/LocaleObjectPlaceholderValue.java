package net.drapuria.framework.language.message.placeholder.object;

import net.drapuria.framework.language.message.placeholder.object.transformer.LocalePlaceholderTransformer;

import java.util.Locale;

public class LocaleObjectPlaceholderValue<O> extends PlaceholderValue<O, LocalePlaceholderTransformer<O>> {

    protected LocaleObjectPlaceholderValue(LocalePlaceholderTransformer<O> transformer) {
        super(transformer);
    }

    protected LocaleObjectPlaceholderValue(Class<O> objectClass, String placeholder, LocalePlaceholderTransformer<O> transformer) {
        super(objectClass, placeholder, transformer);
    }

    public LocaleObjectPlaceholderValue(String placeholder, LocalePlaceholderTransformer<O> transformer) {
        super(placeholder, transformer);
    }

    public LocaleObjectPlaceholderValue(Class<O> objectClass, LocalePlaceholderTransformer<O> transformer) {
        super(objectClass, transformer);
    }

    @Override
    public String getValue(Object toTranslate, Locale locale) {
        return transformer.transform((O) toTranslate, locale);
    }
}