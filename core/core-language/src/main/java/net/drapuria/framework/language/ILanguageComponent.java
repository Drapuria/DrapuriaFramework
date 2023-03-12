package net.drapuria.framework.language;

import java.io.File;

public interface ILanguageComponent<T> {

    T holder();

    File languageFolder();

    default String langFilePrefix() {
        return null;
    }
}
