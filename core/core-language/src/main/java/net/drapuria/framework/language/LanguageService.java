package net.drapuria.framework.language;

import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;

import java.util.*;

@Service(name = "languageService")
public class LanguageService {

    public static LanguageService getService;

    private final Set<LanguageContainer> containers = new HashSet<>();
    private final Set<Locale> registeredLocales = new HashSet<>();

    @PreInitialize
    public void init() {
        getService = this;
        this.registerComponentHolder();
    }

    @PostInitialize
    public void loadInternals() {
    }

    public Optional<LanguageContainer> findContainer(final LanguageHolder<?> holder) {
        return this.containers.stream()
                .filter(languageContainer -> languageContainer.getHolder().equals(holder))
                .findFirst();
    }

    public Optional<LanguageContainer> findContainer(final Object holder) {
        return this.containers.stream()
                .filter(languageContainer -> languageContainer.getHolder().holder().equals(holder))
                .findFirst();
    }

    public String getTranslatedString(final Locale locale, final String key) {

    }

    private void registerComponentHolder() {
        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public void onEnable(Object instance) {
                containers.add(new LanguageContainer((LanguageHolder<?>) instance));
            }

            @Override
            public Class<?>[] type() {
                return new Class[]{LanguageHolder.class};
            }
        });
    }
}