package net.drapuria.framework.language.message.prefix;

import java.util.Locale;

public class SimplePrefixData extends PrefixData<String>{

    public SimplePrefixData(String prefix) {
        super(prefix);
    }

    @Override
    public String getAsString(Locale ignored) {
        return this.getPrefix();
    }
}
