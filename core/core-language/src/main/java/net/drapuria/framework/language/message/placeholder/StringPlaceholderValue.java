package net.drapuria.framework.language.message.placeholder;

import java.util.Locale;

public final class StringPlaceholderValue implements IPlaceholderValue {

    private final String placeholder;
    private final String value;
    public StringPlaceholderValue(String value) {
        this.value = value;
        this.placeholder = null;
    }

    public StringPlaceholderValue(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public String getValue(Object toTranslate, Locale locale) {
        return this.value;
    }
}