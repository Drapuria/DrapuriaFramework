package net.drapuria.framework.language;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.drapuria.framework.util.entry.Entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public final class LanguageFile {

    private final LanguageContainer container;
    @Getter
    private final String isoCode;
    @Getter
    private final File file;
    private Properties properties;
    public LanguageFile(LanguageContainer container, String isoCode, File file) {
        this.container = container;
        this.isoCode = isoCode;
        this.file = file;
    }

    public Properties toProperties() {
        if (this.properties != null)
            return properties;
        final Locale defaultLocale = this.container.getService().getDefaultLocale();
        final String defaultIso = defaultLocale.toLanguageTag().replace("-", "_");
        final Optional<LanguageFile> defaultLanguageFile = this.container.findLanguageFileByIsoCode(defaultIso);
        this.properties = defaultLanguageFile.isPresent() && defaultLanguageFile.get() != this
                ? new Properties(defaultLanguageFile.get().toProperties())
                : new Properties();
        try {
            this.properties.load(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.properties;
    }

    public Map<String, String> readProperties() {
        return Maps.fromProperties(this.toProperties());
    }
}