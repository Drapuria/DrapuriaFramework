package net.drapuria.framework.language.resource;

import net.drapuria.framework.language.LanguageContainer;

import java.util.Locale;

public class LanguageString {

    private final LanguageContainer container;
    private final Locale locale;
    private final String key;
    private String string;

    public LanguageString(LanguageContainer container, Locale locale, String definition, String string) {
        this.container = container;
        this.locale = locale;
        this.key = definition;
        this.string = string;
    }


    public Locale getLocale() {
        return locale;
    }

    public String key() {
        return this.key;
    }

    public String string() {
        return this.string;
    }

    public void setString(final String string) {
        this.string = string;
    }

}