package net.drapuria.framework.language;

import net.drapuria.framework.language.resource.LanguageResource;

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

    public void addResource(final LanguageResource resource) {
        this.resources.put(resource.getLocale(), resource);
    }

    public LanguageResource findResource(final Locale locale) {
        return this.resources.get(locale);
    }
}
