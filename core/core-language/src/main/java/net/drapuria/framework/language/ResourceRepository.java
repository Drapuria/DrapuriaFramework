package net.drapuria.framework.language;

import net.drapuria.framework.language.resource.LanguageResource;
import net.drapuria.framework.language.resource.LanguageString;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResourceRepository {

    private final LanguageService service;
    private final Map<Locale, LanguageResource> resources = new HashMap<>();

    public ResourceRepository(LanguageService service) {
        this.service = service;
    }

    protected void init() {
        if (findResource(this.service.getDefaultLocale()) == null) {
            this.resources.put(this.service.getDefaultLocale(), new LanguageResource(this.service, this.service.getDefaultLocale()));
        }
    }

    protected void loadContainer(final LanguageContainer container) {
        for (LanguageFile languageFile : container.getLanguageFiles()) {
            final Locale locale = Locale.forLanguageTag(languageFile.getIsoCode().replaceAll("_", "-"));
            LanguageResource resource = this.findResource(locale);
            if (resource == null) {
                this.addResource(resource = new LanguageResource(service, locale));
            }
            for (Map.Entry<String, String> entry : languageFile.readProperties().entrySet()) {
                resource.add(entry.getKey(), new LanguageString(container, locale, entry.getKey(), entry.getValue()));
            }
        }
    }

    public void addResource(final LanguageResource resource) {
        this.resources.put(resource.getLocale(), resource);
    }

    public LanguageResource findResource(final Locale locale) {
        return this.resources.get(locale);
    }
}
