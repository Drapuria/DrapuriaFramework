package net.drapuria.framework.language.message.prefix;

import net.drapuria.framework.language.message.AbstractLocalizedMessage;

import java.util.Locale;

public class LocalizedPrefixData extends PrefixData<AbstractLocalizedMessage<?, ?, ?, ?>> {
    public LocalizedPrefixData(AbstractLocalizedMessage<?, ?, ?, ?> prefix) {
        super(prefix);
    }

    @Override
    public String getAsString(Locale locale) {
        return getPrefix().getMessage(locale);
    }
}
