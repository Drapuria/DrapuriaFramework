package net.drapuria.framework.language.message.placeholder;

import java.util.Locale;

public interface IPlaceholderValue {

    String getPlaceholder();

    String getValue(Object toTranslate, Locale locale);
}
