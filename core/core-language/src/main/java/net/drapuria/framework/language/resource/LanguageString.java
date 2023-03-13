package net.drapuria.framework.language.resource;

import net.drapuria.framework.language.LanguageContainer;
import net.drapuria.framework.language.LanguageService;

import java.util.Locale;

public class LanguageString {

    private static final LanguageService languageService = LanguageService.getService;
    private final LanguageContainer container;
    private final Locale locale;
    private final String key;
    private String string;

    public LanguageString(LanguageContainer container, Locale locale, String key, String string) {
        this.container = container;
        this.locale = locale;
        this.key = key;
        this.string = languageService.isBukkit() ? translateAlternativeColor('&', string) : string;
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

    private String translateAlternativeColor(final char altColorChar, final String textToTranslate) { // copied from ChatColor#translateAlternativeColor

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}