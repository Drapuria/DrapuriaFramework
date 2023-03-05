package net.drapuria.framework.language.resource;

import lombok.Getter;
import net.drapuria.framework.language.LanguageService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageResource {

    private final LanguageService service;
    @Getter
    private final Locale locale;
    private final Map<String, LanguageString> strings = new HashMap<>();

    public LanguageResource(LanguageService service, Locale locale) {
        this.service = service;
        this.locale = locale;
    }


    public LanguageString find(final String key) {
        return this.strings.getOrDefault(key, isDefault() ? find("drapuria.string.not.defined")
                : this.service.getResourceRepository().findResource(this.service.getDefaultLocale()).find(key));
    }

    private boolean isDefault() {
        return this.locale.equals(this.service.getDefaultLocale());
    }

    public void add(final String key, final LanguageString languageString) {
        this.strings.put(key, languageString);
    }

