package net.drapuria.framework.language;

import java.io.File;

public interface LanguageHolder<T> {

    T holder();

    File languageFolder();

}
