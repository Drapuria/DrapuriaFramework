package net.drapuria.framework.language;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.language.resource.LanguageResource;
import net.drapuria.framework.language.resource.LanguageString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service(name = "languageService")
public class LanguageService {

    public static LanguageService getService;

    private final ResourceRepository resourceRepository = new ResourceRepository(this);
    private final Map<ILanguageComponent<?>, LanguageContainer> containers = new HashMap<>();

    @Getter
    @Setter
    private Locale defaultLocale = Locale.UK;

    @Getter
    private boolean isBukkit;

    @PreInitialize
    public void init() {
        getService = this;
        this.isBukkit = checkIsBukkit();
        this.registerComponentHolder();
    }

    @PostInitialize
    public void loadInternals() {
        this.resourceRepository.init();
    }

    public Optional<LanguageContainer> findContainer(final ILanguageComponent<?> holder) {
        return Optional.ofNullable(this.containers.get(holder));
    }

    public Optional<LanguageContainer> findContainer(final Object component) {
        return this.containers.values().stream()
                .filter(languageContainer -> languageContainer.getComponent().holder().equals(component))
                .findFirst();
    }

    public String getTranslatedString(final Locale locale, final String key) {
        System.out.println("locale: " + locale);
        final LanguageResource resource = this.resourceRepository.findResource(locale);
        if (resource == null) {
            if (!locale.equals(defaultLocale))
                return getTranslatedString(defaultLocale, key);
            return "lang-not-defined";
        }
        final LanguageString string = resource.find(key);
        return string == null ? "key-not-found" : string.string();
    }

    public Collection<LanguageContainer> getContainers() {
        return containers.values();
    }

    public ResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    private void registerComponentHolder() {
        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public void onEnable(Object instance) {
                final ILanguageComponent<?> languageComponent = (ILanguageComponent<?>) instance;
                containers.put(languageComponent, new LanguageContainer(LanguageService.this, languageComponent));
            }

            @Override
            public Class<?>[] type() {
                return new Class[]{ILanguageComponent.class};
            }
        });
    }

    private boolean checkIsBukkit() {
        try {
            Class.forName("net.drapuria.framework.bukkit.Drapuria");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}