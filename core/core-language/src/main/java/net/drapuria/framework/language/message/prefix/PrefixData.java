package net.drapuria.framework.language.message.prefix;

import java.util.Locale;

public abstract class PrefixData<T> {

    protected final T prefix;

    protected PrefixData(T prefix) {
        this.prefix = prefix;
    }

    public T getPrefix() {
        return prefix;
    }

    public abstract String getAsString(final Locale locale);

}