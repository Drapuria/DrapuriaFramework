package net.drapuria.framework.language;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.drapuria.framework.util.entry.Entry;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Getter
public final class LanguageFile {

    private final LanguageContainer container;
    private final String isoCode;
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
        return defaultLanguageFile.isPresent() && defaultLanguageFile.get() != this
                ? (this.properties = new Properties(defaultLanguageFile.get().toProperties()))
                : (this.properties = new Properties());
    }

    public Map<String, String> readProperties() {
        return Maps.fromProperties(this.toProperties());
    }
}