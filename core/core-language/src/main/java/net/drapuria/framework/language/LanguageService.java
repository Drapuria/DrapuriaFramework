package net.drapuria.framework.language;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;

import java.util.*;

@Service(name = "languageService")
public class LanguageService {

    public static LanguageService getService;

    private final Map<ILanguageComponent<?>, LanguageContainer> containers = new HashMap<>();
    private final Set<Locale> registeredLocales = new HashSet<>();
    @Getter private final ResourceRepository resourceRepository = new ResourceRepository(this);

    @Getter
    @Setter
    private Locale defaultLocale = Locale.US;

    @PreInitialize
    public void init() {
        getService = this;
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
                .filter(languageContainer -> languageContainer.getHolder().holder().equals(component))
                .findFirst();
    }

    public String getTranslatedString(final Locale locale, final String key) {
        return null;
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
}