package net.drapuria.framework.language.holder;

import java.io.Serializable;
import java.util.Locale;

public interface LanguageHolder<ID extends Serializable> {

    Locale getLocale();

    Locale setLocale(final Locale locale);

    ID recognizer();

}