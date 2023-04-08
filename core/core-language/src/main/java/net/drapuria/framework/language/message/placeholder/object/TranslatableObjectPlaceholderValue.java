package net.drapuria.framework.language.message.placeholder.object;

import net.drapuria.framework.language.LanguageService;
import net.drapuria.framework.language.message.placeholder.object.transformer.SimplePlaceholderTransformer;

import java.util.Locale;

public class TranslatableObjectPlaceholderValue<O> extends ObjectPlaceholderValue<O> {

    private final LanguageService languageService;

    protected TranslatableObjectPlaceholderValue(SimplePlaceholderTransformer<O> transformer, LanguageService languageService) {
        super(transformer);
        this.languageService = languageService;
    }

    protected TranslatableObjectPlaceholderValue(Class<O> objectClass, String placeholder, SimplePlaceholderTransformer<O> transformer, LanguageService languageService) {
        super(objectClass, placeholder, transformer);
        this.languageService = languageService;
    }

    public TranslatableObjectPlaceholderValue(String placeholder, SimplePlaceholderTransformer<O> transformer, LanguageService languageService) {
        super(placeholder, transformer);
        this.languageService = languageService;
    }

    public TranslatableObjectPlaceholderValue(Class<O> objectClass, SimplePlaceholderTransformer<O> transformer, LanguageService languageService) {
        super(objectClass, transformer);
        this.languageService = languageService;
    }

    @Override
    public String getValue(Object toTranslate, Locale locale) {
        return languageService.getTranslatedString(locale, super.getValue(toTranslate, locale));
    }
}