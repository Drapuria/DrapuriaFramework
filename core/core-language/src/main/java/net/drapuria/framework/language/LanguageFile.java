package net.drapuria.framework.language;

import lombok.Getter;

import java.io.File;

@Getter
public final class LanguageFile {

    private final String isoCode;
    private final File file;
    public LanguageFile(String isoCode, File file) {
        this.isoCode = isoCode;
        this.file = file;
    }
}
