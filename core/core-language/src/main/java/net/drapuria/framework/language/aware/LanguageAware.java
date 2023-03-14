package net.drapuria.framework.language.aware;

import java.util.Locale;

public interface LanguageAware {

    Locale getLocalization();

    void setLocalization(final Locale locale);
}